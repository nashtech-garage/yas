pipeline {
    agent any

    stages {
        // --- CÁC STAGE CỦA CART SERVICE ---
        stage('Cart - Test & Build') {
            // Điều kiện: Chỉ chạy stage này nếu có thay đổi trong thư mục 'cart/'
            when {
                changeset "cart/**"
            }
            steps {
                echo '=== Phát hiện thay đổi ở Cart Service! ==='
                echo 'Đang chạy Test cho Cart...'
                // Code chạy test (Thành viên 2 sẽ đắp vào sau)
                
                echo 'Đang chạy Build cho Cart...'
                // Code build Docker (Em sẽ viết ở bước tới)
            }
        }

        // --- CÁC STAGE CỦA PRODUCT SERVICE ---
        stage('Product - Test & Build') {
            // Điều kiện: Chỉ chạy stage này nếu có thay đổi trong thư mục 'product/'
            when {
                changeset "product/**" 
            }
            steps {
                echo '=== Phát hiện thay đổi ở Product Service! ==='
                echo 'Đang chạy Test cho Product...'
                echo 'Đang chạy Build cho Product...'
            }
        }

        // --- CÁC STAGE CỦA VETS SERVICE (Theo ví dụ của đề bài) ---
        stage('Vets - Test & Build') {
            when {
                changeset "vets-service/**"
            }
            steps {
                echo '=== Phát hiện thay đổi ở Vets Service! ==='
                echo 'Đang chạy Test và Build cho Vets...'
            }
        }
    }
}