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
inputpath = "./data/exp4/exp4.csv"
inputpath1 = "./data/exp4/exp4-3-10.csv"
inputpath2 = "./data/exp4/exp4-3-nochild.csv"

log_exp4 = "../log/ETE-exp4.txt"

file_list = [inputpath,inputpath1,inputpath2]

for file in file_list:

    datas = read_csv(file)
    
    for i in range(10):
        
        start_time = time.time_ns()

        for data in datas:
            common_names_dict = ncbi.get_common_names(data)
            is_in_ids = any(name in data for name in common_names_dict.values())
            
        end_time = time.time_ns()
        execution_time = end_time - start_time
        print(f"{file}-round{i}:{execution_time} ns")

        write2file(
            log_exp4, f"{file}-round{i}:{execution_time} ns"
        )
