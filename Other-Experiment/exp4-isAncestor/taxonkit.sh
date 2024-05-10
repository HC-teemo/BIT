#!/bin/bash

input_file="./data/exp4.csv"
output_file="../log/taxonkit-isAncestor.txt"

> output_file

for i in {1..240}; do
    tail -n +2 "$input_file" | while IFS=, read -r num1 num2; do
        start_time=$(date +%s%3N)

        result=$(echo "$num1 $num2" | taxonkit lca)

        third_value=$(echo "$result" | awk '{print $3}')

        if [ "$third_value" -eq "$num1" ] || [ "$third_value" -eq "$num2" ]; then
            echo "TRUE"
        else
            echo "FALSE"
        fi

        end_time=$(date +%s%3N)
        execution_time=$((end_time - start_time))
        echo "$num1 - $num2: $execution_time ms" >> "$output_file"
    done
done