ls ./logs/log-* | xargs cat | jq '.|select(.message | contains(" am "))'
