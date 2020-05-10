node {
    stage("Checkout"){
        println 'Checking out from SCM...'
        checkout scm
    }
    withGradle() {
        stage("Compile"){
            String currPath = powershell script:'pwd', returnStout:true
            echo currPath
            println 'Running compilation...'
            dir('./BackendProcessor'){
                println 'Compiling source classes...'
                def allFiles = sh script:'ls', returnStout: true
                echo allFiles
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