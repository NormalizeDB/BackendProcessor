node {
    stage("Checkout"){
        println 'Checking out from SCM...'
        checkout scm
    }
    withGradle('gradle-6.3') {
        stage("Compile"){
            String currPath = powershell 'pwd'
            println 'Current Path: ${currPath}'
            println 'Running compilation...'
            dir('./BackendProcessor'){
                println 'Compiling source classes...'
                sh './gradlew clean build -x test'
                println 'Compiling test classes'
                sh './gradlew testClasses'
            }
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