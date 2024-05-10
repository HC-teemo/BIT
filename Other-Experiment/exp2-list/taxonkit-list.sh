#!/bin/bash

> ../log/taxonkit-list1000.txt

for i in {1..10}; do
    echo "Running iteration $i..."
    tail -n +2 ./data/h6_1000.csv | awk -F',' '{print $1}' | while read -r line; do
        start=$(date +%s%N)
        output=$(taxonkit list --ids "$line")
        end=$(date +%s%N)
        duration=$((end-start))
        duration_ms=$((duration/1000000))
        line_count=$(( $(echo "$output" | wc -l) - 1 ))
        echo "$line: $duration_ms ms, $line_count" >> ../log/taxonkit-list1000.txt
    done
done
