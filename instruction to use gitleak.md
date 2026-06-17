# Code mẫu khi gọi gitleak trong jobs test

    name: "Demo CI with Custom Action"

    on:
        push:
            branches: [main]

    jobs:
        test:
            runs-on: ubuntu-latest
            steps:
                # checkout code
                - name: Checkout
                  uses: actions/checkout@v4
                - name: Setup Java + Sonar cache
                  uses: ./.github/workflows/gitleak
                  with:
                    version: (có thể không cần with này vì em đã default version)
        build:
           ....
