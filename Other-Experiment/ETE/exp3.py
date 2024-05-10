from ete3 import Tree
import sys
from ete3 import NCBITaxa
import random
import csv
import time
        

def read_csv(filename):
    result = []
    with open(filename, newline="") as file:
        reader = csv.reader(file)
        for row in reader:
            row = [int(x) for x in row]
            result.append(row)

    return result

def write2file(filename, content):

    with open(filename, "a") as file:
        file.write(content)
        file.write("\n")

ncbi = NCBITaxa(taxdump_file="../taxdump/taxdump.tar.gz", update=False)
inputpath2 = "./data/exp3/lca2.csv"
inputpath4 = "./data/exp3/lca4.csv"
inputpath8 = "./data/exp3/lca8.csv"
inputpath16 = "./data/exp3/lca16.csv"
inputpath32 = "./data/exp3/lca32.csv"

log_exp3 = "./log/ETE-exp3.txt"

file_list = [inputpath2,inputpath4,inputpath8,inputpath16,inputpath32]

for file in file_list:

    datas = read_csv(file)
    
    for i in range(20):
        
        start_time = time.time_ns()

        for data in datas:
            result = ncbi.get_common_names(data)
            
        end_time = time.time_ns()
        execution_time = end_time - start_time
        print(f"{file}-round{i}:{execution_time} ns")

        write2file(
            log_exp3, f"{file}-round{i}:{execution_time} ns"
        )

