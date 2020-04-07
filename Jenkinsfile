pipeline {
    agent any
    triggers {
        pollSCM 'H 5 * * *'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '1'))
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 1, unit: 'HOURS')
        skipDefaultCheckout true
    }
    tools {
        jdk 'openjdk-1.8'
    }
    stages {
        stage('checkout') {
            steps {
                checkout([$class: 'SubversionSCM',
                    locations: [[remote: 'http://wetator.repositoryhosting.com/svn_public/wetator_wetator/branches/event_controlfinder/wetator', local: '.', depthOption: 'infinity', ignoreExternalsOption: true, cancelProcessOnExternalsFail: true]],
                    quietOperation: true,
                    workspaceUpdater: [$class: 'CheckoutUpdater']])
            }
        }
        stage('build') {
            steps {
                wrap([$class: 'Xvfb']) {
                    withAnt(installation: 'apache-ant-1.10.7') {
                        sh "ant publish-local"
                    }
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'deploy/junit/*.xml'
            recordIssues enabledForFailure: true, sourceCodeEncoding: 'UTF-8', sourceDirectory: 'src', tools: [
                checkStyle(pattern: 'deploy/checkstyle/*.xml', reportEncoding: 'UTF-8'),
                spotBugs(pattern: 'deploy/spotbugs/*.xml', reportEncoding: 'UTF-8', useRankAsPriority: true),
                pmdParser(pattern: 'deploy/pmd/pmd-report.xml', reportEncoding: 'UTF-8'),
                cpd(pattern: 'deploy/pmd/cpd-report.xml', reportEncoding: 'UTF-8'),
                java(),
                javaDoc(),
                taskScanner(includePattern: '**/*.java, **/*.xhtml, **/*.jsp, **/*.html, **/*.js, **/*.css, **/*.xml, **/*.wet, **/*.properties', highTags: 'FIXME, XXX', normalTags: 'TODO')]
            archiveArtifacts artifacts: 'deploy/wetator-*.zip, deploy/wetator-*.jar', allowEmptyArchive: true, fingerprint: true
            step([$class: 'Mailer',
                notifyEveryUnstableBuild: true,
                recipients: "rbri@rbri.de, frank.danek@gmail.com, tobias.woerenkaemper@gmx.com"])
        }
    }
}