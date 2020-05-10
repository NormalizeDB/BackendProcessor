node {
    stage("Checkout"){
        println 'Checking out from SCM...'
        checkout scm
    }
    withGradle() {
        stage("Compile"){
            powershell '$path = pwd; Write-Host $path'
            println 'Running compilation...'
            println 'Compiling source classes...'
            sh './gradlew clean build -x test'
            println 'Compiling test classes'
            sh './gradlew testClasses'
        }
        stage("Test"){
            println 'Running tests...'
            sh './gradlew test'
        }
    }
    stage("Collect Test Results"){
        println 'Collecting Test Results...'
    }
    stage("Mail Test Results"){
        println 'Emailing Test Results to [example@mail.com]...'
    }
}