from ete3 import Tree
import sys
from ete3 import NCBITaxa
import random
import csv
import time

def read_csv(filename):
    row1 = []
    with open(filename, newline="") as file:
        reader = csv.reader(file)
        next(reader)
        for row in reader:
            row = [int(x) for x in row]
            row1.append(row[0])

    return row1

def write2file(filename, content):

    with open(filename, "a") as file:
        file.write(content)
        file.write("\n")

ncbi = NCBITaxa(taxdump_file="../taxdump/taxdump.tar.gz", update=False)
inputpath10 = "./data/exp2/h6_10.csv"
inputpath100 = "./data/exp2/h6_100.csv"
inputpath1000 = "./data/exp2/h6_1000.csv"
inputpath10000 = "./data/exp2/h6_10000.csv"
log_exp2 = "../log/ETE-exp2.txt"

file_list = [inputpath10,inputpath100,inputpath1000,inputpath10000]

for file in file_list:

    datas = read_csv(file)
    
    for i in range(10):
        
        start_time = time.time_ns()

        for data in datas:
            result = ncbi.get_descendant_taxa(parent=data,intermediate_nodes=True)
            
        end_time = time.time_ns()
        execution_time = end_time - start_time
        print(f"{file}-round{i}:{execution_time} ns")

        write2file(
            log_exp2, f"{file}-round{i}:{execution_time} ns"
        )
