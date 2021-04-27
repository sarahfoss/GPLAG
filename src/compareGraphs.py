# -*- coding: utf-8 -*-
"""
Created on Thu Apr  2 13:21:16 2020

@author: Sarah
"""

import pandas as pd 
import matplotlib.pyplot as plt 
import networkx as nx 
from networkx.algorithms import isomorphism
import itertools as it

def createGraph(name):
    gdata_df = pd.read_csv("../res/adjmatrix_"+name+".csv", index_col=0)
    gdata_mat = gdata_df.to_numpy()
    G = nx.from_numpy_matrix(gdata_mat)
    return G

def drawGraph(G):
    
    data = [(u, v) for (u, v, d) in G.edges(data=True) if d['weight'] == 1 or d['weight'] == 3]
    control = [(u, v) for (u, v, d) in G.edges(data=True) if d['weight'] >= 2]
    
    pos = nx.spring_layout(G)  # positions for all nodes
    
    # nodes
    nx.draw_networkx_nodes(G, pos, node_size=300)
    
    # edges
    nx.draw_networkx_edges(G,pos, edgelist=data,
                           width=3)
    nx.draw_networkx_edges(G,pos, edgelist=control,
                           width=3, alpha=0.5, edge_color='b', style='dashed')
    
    plt.show()


def gammaIsomorphic(G1, G2, gamma):
    for subset in it.combinations(list(G2.nodes()), int(gamma*len(G2))):
        #print(subset)
        subG2 = G2.subgraph(subset)
        GM = isomorphism.GraphMatcher(G1,subG2)
        if GM.subgraph_is_isomorphic():
            return True
    return False


#code1 = "code1"
#code2 = "code4"
allCode = ["code1","code1cp", "code2","code3","code4"]

gamma = 0.90
count = 0
isoCount = 0
for i in range(len(allCode)-1):
    for j in range(i+1, len(allCode)):
        
        count += 1
        code1 = allCode[i]
        code2 = allCode[j]

        G1 = createGraph(code1)
        G2 = createGraph(code2)
        
        drawGraph(G1)
        drawGraph(G2)
        
        GM = isomorphism.GraphMatcher(G1,G2)
        if GM.is_isomorphic():
            print("The two graphs from "+code1 + " and " + code2 +" are isomorphic.")
            print("The structures of "+ code1 + " and " + code2 + " seem to be identical.  Please investigate further.")
        else:
            print("The two graphs from "+code1 + " and " + code2 +" are not isomorphic.")
            print("The structures of "+code1 + " and " + code2 +" do not seem to be identical.")
        
        print()
        if gammaIsomorphic(G1, G2, gamma):
            print("A subgraph of "+code2+" with a gamma of "+ str(gamma) +" is subgraph isomorphic to "+code1+".")
            print(code1+ " and " + code2 +" seem to violate the threshold for similarity.  Please investigate further.")
            isoCount += 1
        else:
            print("There is no subgraph of "+code2+" with a gamma of "+ str(gamma) +" that is subgraph isomorphic to "+code1+".")
            print(code1+ " and " + code2 +" seem not to violate the threshold for similarity.")
            

print()
ratio = isoCount/count
print("The ratio of gamma isomorphic code is: " + str(ratio))