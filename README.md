# My Labs
## _Tehnici de programare multiprocesor 2021_
[![N|Solid](https://plati-taxe.uaic.ro/img/logo-retina1.png)](https://www.info.uaic.ro/)

Croitoru Razvan 3A2
Diac P. Gabriel 3A2



## Tema 2

### Continut
- [x] [Exercitiu 1](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/main/README.md#exercitiu-1)
- [x] [Exercitiu 2a](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/main/README.md#exercitiu-2a)
- [x] [Exercitiu 2b](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/main/README.md#exercitiu-2b)
- [x] [Exercitiu 2c](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/main/README.md#exercitiu-2c)
- [x] [Exercitiu 2d](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/main/README.md#exercitiu-2d)
- [x] [Exercitiu 3](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/main/README.md#exercitiu-3)


### Exercitiu 1

Identificati cate o linie de cod din metodele add si remove care corespunde punctelor de linearizare pentru situatiile:
a) adaugare element cu succes in lista :  
[63: `pred.next = node;`](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx1/src/main/java/lists/CoarseList.java#L63)  
b) esec la adaugare element in lista :  
[59: `return false;`](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx1/src/main/java/lists/CoarseList.java#L59)  
c) stergere element cu succes din lista :  
[92: `pred.next = current.next;`](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx1/src/main/java/lists/CoarseList.java#L92)  
d) esec la stergere element din lista :  
[95: `return false;`](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx1/src/main/java/lists/CoarseList.java#L95)  

Cum bucata de cod ce se ocupa cu adaugarea elementelor in lista se afla intr-o sectiune critica, punctul de liniarizare poate fi ales oriunde
in acea bucata de cod. Astfel, identificam linia unde lista se modifica daca operatia are succes (linia 63), respectiv linia unde iesim din bucata critica daca operatia nu are succes (linia 59).  
Aplicam aceeasi logica si pentru functia de stergere a elementelor din lista si identificam liniile 92 pentru succes si 95 pentru esec.

### Exercitiu 2a

Nu este necesar ca in cadrul metodei **enq**, linia `size.getAndIncrement()` sa fie plasata in cadrul sectiunii protejate de **enqLock**.  
Algoritmul va functiona corect in continuare chiar daca plasam linia `size.getAndIncrement()` dupa sectiunea critica, datorita caracterului atomic
al `getAndIncrement()`, care impiedica conflicte cu alti enq-ari.  
Astfel, putem considera operatiile atomice, precum `getAndIncrement()` ca fiind protejate, ele neputand fi observate intr-un stadiu de "in progress" 
de catre vreun thread, orice enq-ar, indiferent de momentul de executie in care se afla, detectand valoare actualizata a size-ului si incrementand-o corect.

