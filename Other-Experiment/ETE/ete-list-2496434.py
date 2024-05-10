from ete3 import Tree
import sys
from ete3 import NCBITaxa
import random
import csv
import time

def write2file(filename, content):
    with open(filename, "a") as file:
        file.write(content)
        file.write("\n")

ncbi = NCBITaxa(taxdump_file="/home/cjw/program/taxonkit/data/taxdump.tar.gz", update=False)
log_exp2 = "/home/cjw/program/taxonkit/ETE/log/log-ETE-exp2-2496434.txt"

for i in range(100):
        
    start_time = time.time_ns()

    result = ncbi.get_descendant_taxa(parent=2496434,intermediate_nodes=True)
        
    end_time = time.time_ns()
    execution_time = end_time - start_time
    print(f"round{i}:{execution_time} ns")

    write2file(
        log_exp2, f"round{i}:{execution_time} ns"
    )
