#! /bin/bash

echo "# Build media, product, sampledata, order"

mvn clean install -pl media -am -DskipTests
mvn clean install -pl product -am -DskipTests
mvn clean install -pl sampledata -am -DskipTests
mvn clean install -pl order -am -DskipTests
docker-compose -f docker-compose.yml build media product sampledata --force-rm --no-cache
#
echo "# Start docker compose instances"
docker compose -f docker-compose.yml up -d

# Usage: ./wait-for-storefront.sh <url> <timeout>

URL=http://storefront/
TIMEOUT=900 # 15 mins

echo -e "\n\n"
echo "2. Waiting for storefront at $URL"

for ((i=1; i<=TIMEOUT; i++)); do
    STATUS=$(curl -o /dev/null -s -w "%{http_code}" "$URL")

    if [[ "$STATUS" == "200" ]]; then
        echo "Storefront is available at $URL!"
        break
    fi

    # Log every 60 seconds
    if (( i % 60 == 0 )); then
        echo "Still waiting for storefront at $URL... (elapsed time: $i seconds)"
    fi

    sleep 1
done

# Check if loop exited without a 200 status
if [[ "$STATUS" != "200" ]]; then
    echo "Timeout reached! Storefront is still not available at $URL."
    exit 1
fi

echo -e "\n\n"
echo "Timeout reached! Storefront at $URL is still not available."

echo -e "\n\n"
echo "3. Start kafka connect"
./start-source-connectors.sh

echo -e "\n\n"
echo "4. Create sampledata"
curl --location 'http://api.yas.local/sampledata/storefront/sampledata' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--header 'Cookie: JSESSIONID=D568506E3D58D1F2F99A4A18CCCEB75D' \
--data '{
    "message": "Create sample data"
}'

exit 1
done