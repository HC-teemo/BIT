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


def is_parent(taxon1, taxon2, taxdb, max_depth=1000, depth=0):
    if depth >= max_depth:
        raise RecursionError("Reached maximum recursion depth")

    parent = taxon1.parent(taxdb)

    if parent is None:
        return False

    if parent.taxid == taxon2.taxid:
        return True

    return is_parent(parent, taxon2, taxdb, max_depth, depth + 1)


def find_root(taxon, taxdb):
    current_taxon = taxon
    taxon_list = []
    taxon_list.append(current_taxon.taxid)
    while True:
        parent_taxon = current_taxon.parent(taxdb)
        if parent_taxon.taxid == 1:
            taxon_list.append(1)
            taxon_list.reverse()
            return taxon_list
        parent_id = parent_taxon.taxid
        taxon_list.append(parent_id)
        current_taxon = parent_taxon


def is_descendant(short_list, long_list):
    short_set = set(short_list)
    long_set = set(long_list)
    return short_set.issubset(long_set)


def write2file(filename, content):

    with open(filename, "a") as file:
        file.write(content)
        file.write("\n")


taxdb = taxopy.TaxDb(
    nodes_dmp="/home/usr/.taxonkit/nodes.dmp",
    names_dmp="/home/usr/.taxonkit/names.dmp",
    keep_files=True,
)

input_file = "./data/exp4.csv"
inputpath1 = "./data/exp4-3-10.csv"
inputpath2 = "./data/exp4-3-nochild.csv"
log_taxonpy_isAncestor = "../log/taxopy-isAncestor.txt"

file_list = [input_file,inputpath1,inputpath2]

for file in file_list:

    datas = importTaxfromfile(file, taxdb)
    write2file(
            log_taxonpy_isAncestor, file
        )

    for i in range(1):
        
        start_time = time.time_ns()

        for taxon in datas:
            result = taxopy.find_lca(taxon, taxdb)

        end_time = time.time_ns()
        execution_time = end_time - start_time
        print(f":{execution_time} ns")
        write2file(
            log_taxonpy_isAncestor, f"round {i}:{execution_time} ns"
        )
