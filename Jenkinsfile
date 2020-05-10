node {

    stage("Checkout"){
        println 'Checking out from SCM...'
        checkout scm
    }
    withGradle(gradleName:'gradle-4.10')
        stage("Compile"){
            String currPath = powershell 'pwd'
            println 'Current Path: ${currPath}'
            println 'Running compilation...'
            dir('./BackendProcessor'){
                println 'Compiling source classes...'
                bat 'gradle clean build -x test'
                println 'Compiling test classes'
                bat 'gradle testClasses'
            }
        }
        stage("Test"){
            println 'Running tests...'
            bat 'gradle test'
        }
    }
    stage("Collect Test Results"){
        println 'Collecting Test Results...'
    }
    stage("Mail Test Results"){
        println 'Emailing Test Results to [example@mail.com]...'
    }
}