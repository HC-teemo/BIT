import taxopy
import random
import csv
from taxopy import Taxon
import time
import subprocess


def read_csv(filename):
    data = []
    with open(filename, newline="") as file:
        reader = csv.reader(file)
        for row in reader:
            row = [int(x) for x in row]
            data.append(tuple(row))
    return data


def importTaxfromfile(filepath, taxdb):
    datas = read_csv(filepath)
    result = []
    temp = []
    for data in datas:
        for taxid in data:
            taxon = Taxon(taxid, taxdb)
            temp.append(taxon)
        result.append(temp)
        temp = []

    return result


def write2file(filename, content):

    with open(filename, "a") as file:
        file.write(content)
        file.write("\n")


taxdb = taxopy.TaxDb(
    nodes_dmp="/home/user/.taxonkit/nodes.dmp",
    names_dmp="/home/user/.taxonkit/names.dmp",
    keep_files=True,
)

lca2_filepath = "./data/lca2.csv"
lca4_filepath = "./data/lca4.csv"
lca8_filepath = "./data/lca8.csv"
lca16_filepath = "./data/lca16.csv"
lca32_filepath = "./data/lca32.csv"
log_taxonpy_lca = "../log/taxopy-addition.txt"

filepath_addition1  = "./data/exp3-addition1.csv"
filepath_addition2  = "./data/exp3-addition2.csv"

file_list = [lca2_filepath,lca4_filepath,lca8_filepath,lca16_filepath,lca32_filepath,filepath_addition1,filepath_addition2]


for file in file_list:

    taxons = importTaxfromfile(file, taxdb)
    for i in range(20):

        start_time = time.time_ns()

        for taxon in taxons:
            result = taxopy.find_lca(taxon, taxdb)

        end_time = time.time_ns()

        execution_time = end_time - start_time
        print(f"{file}:{execution_time} ns")
        write2file(log_taxonpy_lca, f"round{i}:{execution_time} ns")
