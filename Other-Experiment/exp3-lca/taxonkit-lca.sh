#!/bin/bash

input_file="./data/lca2.csv"

for i in {1..10}; do
    
    while IFS= read -r line; do
        start_time=$(date +%s%3N)
        
        modified_line=$(echo "$line" | tr ',' ' ')
        echo "$modified_line" | taxonkit lca 
        
        end_time=$(date +%s%3N)
        execution_time=$((end_time - start_time))
        echo "$input_file:$execution_time ms" >> ../log/taxonkit-lca.txt
    done < "$input_file"

done