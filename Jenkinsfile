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
            bat 'gradle clean build -x test'
            println 'Compiling test classes'
            bat 'gradle testClasses'
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