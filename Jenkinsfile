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
            bat 'call gradlew.bat clean build -x test'
            println 'Compiling test classes'
            bat 'call gradlew.bat testClasses'

        }
    }
    stage("Test"){
        println 'Running tests...'
        bat 'call gradlew.bat test'
    }
    stage("Collect Test Results"){
        println 'Collecting Test Results...'
    }
    stage("Mail Test Results"){
        println 'Emailing Test Results to [example@mail.com]...'
    }
}