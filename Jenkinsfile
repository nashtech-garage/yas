pipeline {
    agent any

    options {
        // Keeps only the last 10 builds to save disk space
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    triggers {
        // Scan and run the pipeline for each branch automatically
        pollSCM('* * * * *')
    }

    tools {
        jdk 'JDK-21'
        maven 'Maven-3'
    }

    stages {
        stage('Services CI') {
            parallel {
                
                stage('common-library') {
                    when { changeset "common-library/**" }
                    stages {
                        stage('common-library - Test') {
                            steps { sh 'mvn -B clean test -pl common-library -am' }
                            post { always { junit testResults: 'common-library/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'common-library/target/jacoco.exec' } }
                        }
                        stage('common-library - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl common-library -am' }
                        }
                    }
                }

                stage('backoffice-bff') {
                    when { changeset "backoffice-bff/**" }
                    stages {
                        stage('backoffice-bff - Test') {
                            steps { sh 'mvn -B clean test -pl backoffice-bff -am' }
                            post { always { junit testResults: 'backoffice-bff/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'backoffice-bff/target/jacoco.exec' } }
                        }
                        stage('backoffice-bff - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl backoffice-bff -am' }
                        }
                    }
                }

                stage('cart') {
                    when { changeset "cart/**" }
                    stages {
                        stage('cart - Test') {
                            steps { sh 'mvn -B clean test -pl cart -am' }
                            post { always { junit testResults: 'cart/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'cart/target/jacoco.exec' } }
                        }
                        stage('cart - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl cart -am' }
                        }
                    }
                }

                stage('customer') {
                    when { changeset "customer/**" }
                    stages {
                        stage('customer - Test') {
                            steps { sh 'mvn -B clean test -pl customer -am' }
                            post { always { junit testResults: 'customer/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'customer/target/jacoco.exec' } }
                        }
                        stage('customer - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl customer -am' }
                        }
                    }
                }

                stage('inventory') {
                    when { changeset "inventory/**" }
                    stages {
                        stage('inventory - Test') {
                            steps { sh 'mvn -B clean test -pl inventory -am' }
                            post { always { junit testResults: 'inventory/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'inventory/target/jacoco.exec' } }
                        }
                        stage('inventory - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl inventory -am' }
                        }
                    }
                }

                stage('location') {
                    when { changeset "location/**" }
                    stages {
                        stage('location - Test') {
                            steps { sh 'mvn -B clean test -pl location -am' }
                            post { always { junit testResults: 'location/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'location/target/jacoco.exec' } }
                        }
                        stage('location - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl location -am' }
                        }
                    }
                }

                stage('media') {
                    when { changeset "media/**" }
                    stages {
                        stage('media - Test') {
                            steps { sh 'mvn -B clean test -pl media -am' }
                            post { always { junit testResults: 'media/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'media/target/jacoco.exec' } }
                        }
                        stage('media - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl media -am' }
                        }
                    }
                }

                stage('order') {
                    when { changeset "order/**" }
                    stages {
                        stage('order - Test') {
                            steps { sh 'mvn -B clean test -pl order -am' }
                            post { always { junit testResults: 'order/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'order/target/jacoco.exec' } }
                        }
                        stage('order - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl order -am' }
                        }
                    }
                }

                stage('payment-paypal') {
                    when { changeset "payment-paypal/**" }
                    stages {
                        stage('payment-paypal - Test') {
                            steps { sh 'mvn -B clean test -pl payment-paypal -am' }
                            post { always { junit testResults: 'payment-paypal/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'payment-paypal/target/jacoco.exec' } }
                        }
                        stage('payment-paypal - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl payment-paypal -am' }
                        }
                    }
                }

                stage('payment') {
                    when { changeset "payment/**" }
                    stages {
                        stage('payment - Test') {
                            steps { sh 'mvn -B clean test -pl payment -am' }
                            post { always { junit testResults: 'payment/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'payment/target/jacoco.exec' } }
                        }
                        stage('payment - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl payment -am' }
                        }
                    }
                }

                stage('product') {
                    when { changeset "product/**" }
                    stages {
                        stage('product - Test') {
                            steps { sh 'mvn -B clean test -pl product -am' }
                            post { always { junit testResults: 'product/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'product/target/jacoco.exec' } }
                        }
                        stage('product - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl product -am' }
                        }
                    }
                }

                stage('promotion') {
                    when { changeset "promotion/**" }
                    stages {
                        stage('promotion - Test') {
                            steps { sh 'mvn -B clean test -pl promotion -am' }
                            post { always { junit testResults: 'promotion/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'promotion/target/jacoco.exec' } }
                        }
                        stage('promotion - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl promotion -am' }
                        }
                    }
                }

                stage('rating') {
                    when { changeset "rating/**" }
                    stages {
                        stage('rating - Test') {
                            steps { sh 'mvn -B clean test -pl rating -am' }
                            post { always { junit testResults: 'rating/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'rating/target/jacoco.exec' } }
                        }
                        stage('rating - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl rating -am' }
                        }
                    }
                }

                stage('search') {
                    when { changeset "search/**" }
                    stages {
                        stage('search - Test') {
                            steps { sh 'mvn -B clean test -pl search -am' }
                            post { always { junit testResults: 'search/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'search/target/jacoco.exec' } }
                        }
                        stage('search - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl search -am' }
                        }
                    }
                }

                stage('storefront-bff') {
                    when { changeset "storefront-bff/**" }
                    stages {
                        stage('storefront-bff - Test') {
                            steps { sh 'mvn -B clean test -pl storefront-bff -am' }
                            post { always { junit testResults: 'storefront-bff/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'storefront-bff/target/jacoco.exec' } }
                        }
                        stage('storefront-bff - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl storefront-bff -am' }
                        }
                    }
                }

                stage('tax') {
                    when { changeset "tax/**" }
                    stages {
                        stage('tax - Test') {
                            steps { sh 'mvn -B clean test -pl tax -am' }
                            post { always { junit testResults: 'tax/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'tax/target/jacoco.exec' } }
                        }
                        stage('tax - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl tax -am' }
                        }
                    }
                }

                stage('webhook') {
                    when { changeset "webhook/**" }
                    stages {
                        stage('webhook - Test') {
                            steps { sh 'mvn -B clean test -pl webhook -am' }
                            post { always { junit testResults: 'webhook/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'webhook/target/jacoco.exec' } }
                        }
                        stage('webhook - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl webhook -am' }
                        }
                    }
                }

                stage('sampledata') {
                    when { changeset "sampledata/**" }
                    stages {
                        stage('sampledata - Test') {
                            steps { sh 'mvn -B clean test -pl sampledata -am' }
                            post { always { junit testResults: 'sampledata/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'sampledata/target/jacoco.exec' } }
                        }
                        stage('sampledata - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl sampledata -am' }
                        }
                    }
                }

                stage('recommendation') {
                    when { changeset "recommendation/**" }
                    stages {
                        stage('recommendation - Test') {
                            steps { sh 'mvn -B clean test -pl recommendation -am' }
                            post { always { junit testResults: 'recommendation/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'recommendation/target/jacoco.exec' } }
                        }
                        stage('recommendation - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl recommendation -am' }
                        }
                    }
                }

                stage('delivery') {
                    when { changeset "delivery/**" }
                    stages {
                        stage('delivery - Test') {
                            steps { sh 'mvn -B clean test -pl delivery -am' }
                            post { always { junit testResults: 'delivery/target/surefire-reports/*.xml', allowEmptyResults: true; jacoco execPattern: 'delivery/target/jacoco.exec' } }
                        }
                        stage('delivery - Build') {
                            steps { sh 'mvn -B clean package -DskipTests -pl delivery -am' }
                        }
                    }
                }

            }
        }
    }
}
