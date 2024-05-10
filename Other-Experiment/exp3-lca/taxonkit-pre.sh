#!/bin/bash

for i in {1..100};do
    start_time=$(date +%s%3N)
        
    echo 1 1 | taxonkit lca 
    
    end_time=$(date +%s%3N)
    execution_time=$((end_time - start_time))
    echo "$i:$execution_time ms" >> ../log/taxonkit-lca-pre.txt

done 
