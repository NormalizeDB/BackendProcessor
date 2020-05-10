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
            bat 'gradle wrapper'
            bat 'gradlew clean build -x test'
            println 'Compiling test classes'
            bat 'gradlew testClasses'
        }
        stage("Test"){
            println 'Running tests...'
            bat 'gradlew test'
        }
    }
    stage("Collect Test Results"){
        println 'Collecting Test Results...'
    }
    stage("Mail Test Results") {
        println 'Emailing Test Results to default recipients...'
        mail to:'mmelk057@uottawa.ca', subject:'BackendProcessor Build', body: 'Build Passed!'
    }
}