pipeline {
    agent any

    tools {
        jdk 'jdk25'
        maven 'maven3'
    }

    stages {
        stage('Build Common Library') {
            steps {
                echo 'Đang Build Common Library...'
                sh 'mvn clean install -pl common-library -am'
            }
        }

        stage('Build & Test All Services') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE_NAME'
                        values 'media', 'product', 'cart', 'location', 'order', 'customer', 'rating', 'inventory', 'tax', 'search'
                    }
                }
                stages {
                    stage('Build Phase') {
                        when {
                            anyOf {
                                changeset "${SERVICE_NAME}/**"
                                environment name: 'FORCE_BUILD_ALL', value: 'true'
                            }
                        }
                        steps {
                            echo "Đang Build service: ${SERVICE_NAME}..."
                            lock('maven-build') {
                                sh "mvn compile -pl ${SERVICE_NAME}"
                            }
                        }
                    }
                    stage('Test Phase') {
                        when {
                            anyOf {
                                changeset "${SERVICE_NAME}/**"
                                environment name: 'FORCE_BUILD_ALL', value: 'true'
                            }
                        }
                        steps {
                            echo "Đang Test và Đo lường độ phủ cho service: ${SERVICE_NAME}..."
                            lock('maven-build') {
                                sh "mvn org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report -pl ${SERVICE_NAME} -Dserver.port=0 -Dspring.jmx.enabled=false" 
                            }
                        }
                        post {
                            always {
                                junit allowEmptyResults: true, 
                                      testResults: "${SERVICE_NAME}/target/surefire-reports/*.xml"
                                      
                                jacoco(
                                    execPattern: "${SERVICE_NAME}/target/jacoco.exec",
                                    classPattern: "${SERVICE_NAME}/target/classes",
                                    sourcePattern: "${SERVICE_NAME}/src/main/java",
                                    exclusionPattern: '**/config/**,**/exception/**,**/constants/**,**/*Application.class', 
                                    changeBuildStatus: true,
                                    minimumLineCoverage: '70', 
                                    maximumLineCoverage: '70'       
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}