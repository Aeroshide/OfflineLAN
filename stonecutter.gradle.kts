plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["version"] = "\"${property("mod.version")}\";"
}


tasks.register("testAllRuntime") {
    group = "verification"
    description = "Sequentially runs runClient on all 8 Stonecutter versions to verify mod up until initialization."
    notCompatibleWithConfigurationCache("Dynamic process execution of sub-project runClient tasks")

    doLast {
        val versions = listOf("26.3", "26.2", "26.1.2", "1.19.4", "1.18.2", "1.17.1", "1.16.5", "1.14.4")
        val runDir = file("run")
        if (!runDir.exists()) runDir.mkdirs()
        file("run/eula.txt").writeText("eula=true\n")

        val buildDir = file("build")
        if (!buildDir.exists()) buildDir.mkdirs()

        val isWindows = System.getProperty("os.name").lowercase().contains("win")
        val gradlewCmd = if (isWindows) "gradlew.bat" else "./gradlew"

        println("Starting Test")

        val results = mutableMapOf<String, String>()

        fun killProcessTree(process: Process) {
            val isWin = System.getProperty("os.name").lowercase().contains("win")
            try {
                if (isWin) {
                    // Tree kill cmd.exe and gradlew
                    ProcessBuilder("taskkill", "/F", "/T", "/PID", process.pid().toString()).start().waitFor()
                    // Target and kill any spawned Minecraft/Fabric client process directly
                    val psKill = ProcessBuilder(
                        "powershell", "-NoProfile", "-Command",
                        "Get-CimInstance Win32_Process | Where-Object { \$_.CommandLine -like '*KnotClient*' -or \$_.CommandLine -like '*devlaunchinjector*' -or \$_.CommandLine -like '*net.minecraft.client.main.Main*' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }"
                    )
                    psKill.start().waitFor()
                } else {
                    process.toHandle().descendants().forEach { it.destroyForcibly() }
                    process.destroyForcibly()
                }
            } catch (ignored: Exception) {
                process.destroyForcibly()
            }
            Thread.sleep(3000)
        }

        for (v in versions) {
            println("Testing :$v:runClient ...")
            val logFile = file("build/test_client_$v.log")
            if (logFile.exists()) logFile.delete()

            val processBuilder = ProcessBuilder(
                if (isWindows) listOf("cmd.exe", "/c", "$gradlewCmd --no-daemon :$v:runClient > \"${logFile.absolutePath}\" 2>&1")
                else listOf("sh", "-c", "$gradlewCmd --no-daemon :$v:runClient > \"${logFile.absolutePath}\" 2>&1")
            )
            processBuilder.directory(rootDir)
            val process = processBuilder.start()

            var passed = false
            var failed = false
            var failureReason = ""

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 120000) {
                Thread.sleep(1000)
                if (logFile.exists()) {
                    val logText = logFile.readText()

                    val reachedTitleScreen = logText.contains("Realms Notification") || logText.contains("textures/atlas") || logText.contains("textures-atlas") || logText.contains("Sound engine started") || logText.contains("Loaded 0 advancements")
                    if (reachedTitleScreen) {
                        passed = true
                        failureReason = "Success"
                        killProcessTree(process)
                        break
                    }

                    if (logText.contains("BUILD FAILED") || logText.contains("Game crashed!") || logText.contains("InvalidMixinException") || logText.contains("Mixin apply") || logText.contains("Uncaught exception")) {
                        failed = true
                        val lines = logText.lines()
                        val errLines = lines.filter { (it.contains("Caused by:") || it.contains("Exception:") || it.contains("BUILD FAILED") || it.contains("Error:") || it.contains("InvalidMixinException")) && !it.contains("Fernflower") && !it.contains("NoClassDefFoundError: org/jetbrains/java/decompiler") }.take(2)
                        failureReason = if (errLines.isNotEmpty()) errLines.joinToString(" | ") else "Compilation/Runtime Crash"
                        killProcessTree(process)
                        break
                    }
                }
                if (!process.isAlive) {
                    if (!passed && !failed) {
                        failed = true
                        val logText = if (logFile.exists()) logFile.readText() else ""
                        val lines = logText.lines()
                        val errLines = lines.filter { (it.contains("Caused by:") || it.contains("Exception:") || it.contains("BUILD FAILED") || it.contains("Error:")) && !it.contains("Fernflower") && !it.contains("NoClassDefFoundError: org/jetbrains/java/decompiler") }.take(2)
                        failureReason = if (errLines.isNotEmpty()) errLines.joinToString(" | ") else "Process exited unexpectedly with code ${process.exitValue()}"
                    }
                    break
                }
            }

            if (process.isAlive) {
                killProcessTree(process)
            }

            val status = if (passed) "PASS" else "FAIL"
            results[v] = "$status | Reason: $failureReason"
            println("[$v] -> $status | Reason: $failureReason (Log: build/test_client_$v.log)")
        }

        println("\n================ FINAL SUMMARY TABLE ================")
        results.forEach { (v, res) ->
            println("${v.padEnd(10)} : $res")
        }

        val failedVersions = results.filter { it.value.startsWith("FAIL") }
        if (failedVersions.isNotEmpty()) {
            throw GradleException("Runtime client test failed for versions: ${failedVersions.keys.joinToString(", ")}")
        }
    }
}
