node {
    stage("Checkout"){
        println 'Checking out from SCM...'
        checkout scm
    }
    stage("Compile"){
        String currPath = powershell 'pwd'
        println 'Current Path: ${currPath}'
        println 'Running compilation...'
        dir('./BackendProcessor'){
            println 'Compiling source classes...'
            bat 'gradlew clean build -x test'
            println 'Compiling test classes'
            bat 'gradlew testClasses'

        }
    }
    stage("Test"){
        println 'Running tests...'
        bat 'gradlew test'
    }
    stage("Collect Test Results"){
        println 'Collecting Test Results...'
    }
    stage("Mail Test Results"){
        println 'Emailing Test Results to [example@mail.com]...'
    }
}