#!/usr/bin/env sh
readonly containerName="$1"

# Check every 1s for 30s that the container becomes healthy
for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30
do
    health=$(docker inspect --format='{{.State.Health.Status}}' "$containerName")
    echo "Health = $health"

    if [ "${health}" = "healthy" ]; then
        exit 0
    fi

    sleep 1
done

echo "Container did not become healthy within 30s"
exit 1
