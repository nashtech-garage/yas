#! /bin/bash

#echo "1. Start docker compose instances"
#docker compose -f docker-compose.yml up -d

# Usage: ./wait-for-storefront.sh <url> <timeout>

URL=http://storefront/
TIMEOUT=900 # 15 mins

echo -e "\n\n"
echo "2. Waiting for storefront at $URL"

for ((i=0; i<TIMEOUT; i++)); do
    STATUS=$(curl -o /dev/null -s -w "%{http_code}" "$URL")
    if [[ "$STATUS" == "200" ]]; then
        echo "Storefront is available at $URL!"
    fi

    if (( i % 60 == 0 )); then
            echo "Still waiting for storefront at $URL... (elapsed time: $i seconds)"
    fi

    sleep 1

echo -e "\n\n"
echo "Timeout reached! Storefront at $URL is still not available."

echo -e "\n\n"
echo "3. Start kafka connect"
./start-source-connectors.sh

echo -e "\n\n"
echo "4. Create sampledata"
curl --location 'http://localhost:8094/sampledata/storefront/sampledata' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--header 'Cookie: JSESSIONID=D568506E3D58D1F2F99A4A18CCCEB75D' \
--data '{
    "message": "Create sample data"
}'

exit 1
done