#!/bin/bash

> ../log/taxonkit-list-pre.txt

for i in {1..100};do
    start_time=$(date +%s%3N)
        
    taxonkit list --ids 2496434
    
    end_time=$(date +%s%3N)
    execution_time=$((end_time - start_time))
    echo "$i:$execution_time ms" >> ../log/taxonkit-list-pre.txt

done 
