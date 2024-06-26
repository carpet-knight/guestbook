import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.CustomChart
import jetbrains.buildServer.configs.kotlin.CustomChart.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectCustomChart
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.03"

project {

    subProject(Build)
    subProject(Deploy)
}


object Build : Project({
    name = "Build"

    buildType(Build_DeployImage)
    buildType(Build_BuildFrontend)
    buildType(Build_BuildBackendImage)
    buildType(Build_BuildBackend)
    buildType(Build_BuildFrontendImage)

    features {
        projectCustomChart {
            id = "PROJECT_EXT_15"
            title = "New chart title"
            seriesTitle = "Serie"
            format = CustomChart.Format.TEXT
            series = listOf(
                Serie(title = "Queue wait reason: No available agents", key = SeriesKey("queueWaitReason:No_available_agents"), sourceBuildTypeId = "GuestbookAws_Build_BuildBackend")
            )
        }
    }
})

object Build_BuildBackend : BuildType({
    name = "Build Backend"

    buildNumberPattern = "1.0.%build.counter%"

    vcs {
        root(DslContext.settingsRoot, "+:backend => backend")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        gradle {
            tasks = "teamcity"
            buildFile = "build.gradle.kts"
            workingDir = "backend"
            dockerImage = "openjdk:11-jdk"
        }
    }
})

object Build_BuildBackendImage : BuildType({
    name = "Build Backend Image"

    buildNumberPattern = "${Build_BuildBackend.depParamRefs.buildNumber}"

    vcs {
        root(DslContext.settingsRoot, "+:backend => backend")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Build Image"
            id = "Build_Image"
            scriptContent = "echo Emulate Build Backend Image"
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    dependencies {
        dependency(Build_BuildBackend) {
            snapshot {
                synchronizeRevisions = false
            }

            artifacts {
                cleanDestination = true
                artifactRules = "*.jar => backend/build"
            }
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
    }
})

object Build_BuildFrontend : BuildType({
    name = "Build Frontend"

    artifactRules = "frontend/dist => dist"
    buildNumberPattern = "1.0.%build.counter%"

    vcs {
        root(DslContext.settingsRoot, "+:frontend => frontend")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        nodeJS {
            workingDir = "frontend"
            shellScript = """
                npm ci
                npm run build
            """.trimIndent()
        }
    }
})

object Build_BuildFrontendImage : BuildType({
    name = "Build Frontend Image"

    buildNumberPattern = "${Build_BuildFrontend.depParamRefs.buildNumber}"

    vcs {
        root(DslContext.settingsRoot, "+:frontend => frontend")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Build Image"
            scriptContent = "echo Emulate Build Frontend Image"
        }
    }

    dependencies {
        dependency(Build_BuildFrontend) {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                synchronizeRevisions = false
            }

            artifacts {
                cleanDestination = true
                artifactRules = "dist/** => frontend/docker/dist"
            }
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
    }
})

object Build_DeployImage : BuildType({
    name = "Build Deploy Image"

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Build Image"
            scriptContent = "echo Emulate Build Deploy Image"
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
    }
})


object Deploy : Project({
    name = "Deploy"

    buildType(Deploy_DeployStaging)
    buildType(Deploy_BuildRdImage)
    buildType(Deploy_BuildBastion)
    buildType(Deploy_DeployInfrastructure)
    buildType(Deploy_DeployProduction)
    buildTypesOrder = arrayListOf(Deploy_DeployInfrastructure, Deploy_DeployStaging, Deploy_DeployProduction, Deploy_BuildBastion, Deploy_BuildRdImage)
})

object Deploy_BuildBastion : BuildType({
    name = "Build Bastion Host AMI"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "packer"
            scriptContent = "echo Emulate Build Bastion Host AMI"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerPull = true
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
        equals("docker.server.osType", "linux")
    }
})

object Deploy_BuildRdImage : BuildType({
    name = "Build Remote Development AMI"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "packer"
            scriptContent = "echo Emulate Build Remote Development AMI"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerPull = true
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
    }
})

object Deploy_DeployInfrastructure : BuildType({
    name = "Deploy Infrastructure"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "deploy"
            scriptContent = "echo Emulate Deploy Infrastructure"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerPull = true
            dockerRunParameters = "--rm -v /var/run/docker.sock:/var/run/docker.sock"
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
    }
})

object Deploy_DeployProduction : BuildType({
    name = "Deploy to Production"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "deploy"
            scriptContent = "echo Emulate Deploy to Production"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerPull = true
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    dependencies {
        snapshot(Build_BuildBackendImage) {
            synchronizeRevisions = false
        }
        snapshot(Build_BuildFrontendImage) {
            synchronizeRevisions = false
        }
        snapshot(Deploy_DeployStaging) {
            synchronizeRevisions = false
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
    }
})

object Deploy_DeployStaging : BuildType({
    name = "Deploy to Staging"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "deploy"
            scriptContent = "echo Emulate Deploy to Staging"
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerPull = true
        }
    }

    triggers {
        vcs {
            triggerRules = "-:.teamcity/**"
            branchFilter = """
                +:*
                -:master
            """.trimIndent()
            watchChangesInDependencies = true
        }
    }

    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    dependencies {
        snapshot(Build_BuildBackendImage) {
            synchronizeRevisions = false
        }
        snapshot(Build_BuildFrontendImage) {
            synchronizeRevisions = false
        }
    }

    requirements {
        equals("docker.server.osType", "linux")
    }
})
