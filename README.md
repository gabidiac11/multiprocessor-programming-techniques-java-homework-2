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

### Exercitiu 2

Pentru fiecare subpunct al exercitiului 2, am creat cate un algoritm care sa reproduca pseudocodul corespunzator. In afara de niste schimbari pentru ca algorimii sa compileze (semnalate si de alti colegi), in plus fata de functionalitatea originala am adaugat print-uri care sa evidentieze traseul thread-urilor. Pentru anumite subpuncte au fost adaugate si alte lucruri care tin tot de printare (variabile locale, proprietati, folosite strict pentru a printa un eveniment pe ecran) si vor fi mentionate in dreptul subpunctelor corespunzatoare.

Pentru a rula algorimii dispunem de o [clasa](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx2/src/main/java/ThreadsRun.java#L6) care instantiaza, dupa caz, implementarea cozii corespunzatoare a unui subpunct. Am rulat cu ~30, ~100, ~1000 si ~10_000 de operatii pentru cozi cu capacitate de 1, 2, 5 si 20. Ordinea operatiilor este nedeterminista (50-50), folosind Random. In acelasi timp, am dorit ca numarul operatiilor de deq sa fie egal cu cel de enq, asa ca, dupa alegerea nedeterminista, am "umplut" dupa caz cu cat mai este nevoie pentru ca la sfarsit numarul de operatii enq si deq sa fie egal, iar executia sa se incheie de la sine, in caz de succes. Tuturor implementarilor le-au fost adaugate 2 proprietati `ouputE`, `ouputD`, (ca exemplu [aici](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx2/src/main/java/a/BoundedQueue.java#L34)), in incercarea de a surprinde mai bine cand un thread a obtinut lock-ul.

### Exercitiu 2a

[Implementare](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx2/src/main/java/a/BoundedQueue.java#L9)

Nu este necesar ca in cadrul metodei **enq**, linia `size.getAndIncrement()` sa fie plasata in cadrul sectiunii protejate de **enqLock**.  
Algoritmul va functiona corect in continuare chiar daca plasam linia `size.getAndIncrement()` dupa sectiunea critica, datorita caracterului atomic
al `getAndIncrement()`, care impiedica conflicte cu alti enq-ari.  
Astfel, putem considera operatiile atomice, precum `getAndIncrement()` ca fiind protejate, ele neputand fi observate intr-un stadiu de "in progress" 
de catre vreun thread, orice enq-ar, indiferent de momentul de executie in care se afla, detectand valoare actualizata a size-ului si incrementand-o corect.

### Exercitiu 2b

[Implementare](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx2/src/main/java/b/BoundedQueue.java#L8)

Metoda enq nu va mai functiona corect, nemaipastrandu-se caracterul FIFO al cozii intrucat in timp ce un enq-ar s-ar afla in sectiunea critica,
dupa obtinerea lock-ului din **head**, un thread deq-ar ar putea modifica **head** sa pointeze catre alt nod, urmand ca thread-ul enq-ar sa incerce
sa deblocheze un alt lacat decat cel pe care l-a blocat initial.

Exemplu:  
Rulam algoritmul cu 30 de threaduri si o coada de capacitate 1, adaugand un id pe fiecare nod, folosind un counter.

Dequeuer-ul 'Thread-7' obtine lock-ul pe nodul tail (id - 1), iar pana sa ajunga la unlock, nodul (id - 2) devine noul tail,
'Thread-7' incercand deblocarea unui alt lock, aruncand o exceptie (**IllegalMonitorStateException**)

---

<details>
    <summary>
    Output-ul problemei de deblocare a altui lacat
    </summary>
-----------------Started test non-det [1 capacity, 30 num of threads]-----------------

DEQ: 'Thread-0' wants lock   
DEQ: 'Thread-1' wants lock   
DEQ: 'Thread-0' gets lock node-id-0  

DEQ: 'Thread-0' awaits notEmptyCondition   
DEQ: 'Thread-1' gets lock node-id-0    

DEQ: 'Thread-1' awaits notEmptyCondition  
ENQ: 'Thread-2' wants lock  
ENQ: 'Thread-3' wants lock  
ENQ: 'Thread-4' wants lock  
ENQ: 'Thread-2' gets lock node-id-0  
ENQ: 'Thread-5' wants lock  
ENQ: 'Thread-6' wants lock  
ENQ: 'Thread-2' added a new node 2, id-1  
ENQ: 'Thread-8' wants lock  
DEQ: 'Thread-7' wants lock  
DEQ: 'Thread-10' wants lock  
ENQ: 'Thread-12' wants lock  
ENQ: 'Thread-2' must wake dequeuers  
ENQ: 'Thread-9' wants lock  
ENQ: 'Thread-14' wants lock  
ENQ: 'Thread-2' releases lock  
DEQ: 'Thread-13' wants lock  
DEQ: 'Thread-7' gets lock node-id-1  

DEQ: 'Thread-11' wants lock  
DEQ: 'Thread-19' wants lock  
DEQ: 'Thread-7' removes node 2, id-1  
DEQ: 'Thread-18' wants lock  
ENQ: 'Thread-3' gets lock node-id-0  
ENQ: 'Thread-17' wants lock  
ENQ: 'Thread-2' wants DEQ-lock  
DEQ: 'Thread-16' wants lock  
ENQ: 'Thread-15' wants lock  
DEQ: 'Thread-27' wants lock  
ENQ: 'Thread-2' gets DEQ-lock node-id-2  

DEQ: 'Thread-26' wants lock  
DEQ: 'Thread-25' wants lock  
ENQ: 'Thread-24' wants lock  
ENQ: 'Thread-3' added a new node 3, id-2  
ENQ: 'Thread-3' must wake dequeuers  
DEQ: 'Thread-23' wants lock  
DEQ: 'Thread-22' wants lock  
DEQ: 'Thread-7' must wake enqueuers  
DEQ: 'Thread-21' wants lock  
DEQ: 'Thread-20' wants lock  
DEQ: 'Thread-7' may be about to release lock id-2, but has lock for node-id-1  
ENQ: 'Thread-3' may be about to release lock id-1, but has lock for node-id-0  
ENQ: 'Thread-29' wants lock  
ENQ: 'Thread-2' releases DEQ-lock  
ENQ: 'Thread-28' wants lock  
DEQ: 'Thread-16' gets lock node-id-2  

DEQ: 'Thread-16' removes node 3, id-2  
DEQ: 'Thread-16' must wake enqueuers  
DEQ: 'Thread-16' releases lock  
DEQ: 'Thread-16' wants ENQ-lock  
DEQ: 'Thread-27' gets lock node-id-2  

DEQ: 'Thread-27' awaits notEmptyCondition  
DEQ: 'Thread-26' gets lock node-id-2  

DEQ: 'Thread-26' awaits notEmptyCondition  
DEQ: 'Thread-25' gets lock node-id-2  

DEQ: 'Thread-25' awaits notEmptyCondition  
DEQ: 'Thread-23' gets lock node-id-2  

DEQ: 'Thread-23' awaits notEmptyCondition  
DEQ: 'Thread-22' gets lock node-id-2  

DEQ: 'Thread-22' awaits notEmptyCondition  
DEQ: 'Thread-21' gets lock node-id-2  

ENQ: 'Thread-30' wants lock  
ENQ: 'Thread-31' wants lock  
DEQ: 'Thread-21' awaits notEmptyCondition  
DEQ: 'Thread-20' gets lock node-id-2  

DEQ: 'Thread-20' awaits notEmptyCondition  
DEQ: 'Thread-16' gets ENQ-lock node-id-2  

DEQ: 'Thread-16' releases ENQ-lock  
ENQ: 'Thread-30' gets lock node-id-2  
ENQ: 'Thread-30' added a new node 30, id-3  
ENQ: 'Thread-30' must wake dequeuers  
ENQ: 'Thread-30' releases lock  
ENQ: 'Thread-30' wants DEQ-lock  
ENQ: 'Thread-31' gets lock node-id-2  
ENQ: 'Thread-31' awaits notFullCondition  
ENQ: 'Thread-30' gets DEQ-lock node-id-3  

ENQ: 'Thread-30' releases DEQ-lock  
DEQ: 'Thread-27' removes node 30, id-3  
DEQ: 'Thread-27' must wake enqueuers  
DEQ: 'Thread-27' may be about to release lock id-3, but has lock for node-id-2  
Exception in thread "Thread-7" Exception in thread "Thread-3" java.lang.IllegalMonitorStateException  
at java.base/java.util.concurrent.locks.ReentrantLock$Sync.tryRelease(ReentrantLock.java:175)  
at java.base/java.util.concurrent.locks.AbstractQueuedSynchronizer.release(AbstractQueuedSynchronizer.java:1007)  
at java.base/java.util.concurrent.locks.ReentrantLock.unlock(ReentrantLock.java:494)  
at b.BoundedQueue.enq(BoundedQueue.java:55)  
at ThreadsRun.lambda$RunTest$0(ThreadsRun.java:50)  
at java.base/java.lang.Thread.run(Thread.java:833)  
java.lang.IllegalMonitorStateException  
at java.base/java.util.concurrent.locks.ReentrantLock$Sync.tryRelease(ReentrantLock.java:175)  
at java.base/java.util.concurrent.locks.AbstractQueuedSynchronizer.release(AbstractQueuedSynchronizer.java:1007)  
at java.base/java.util.concurrent.locks.ReentrantLock.unlock(ReentrantLock.java:494)  
at b.BoundedQueue.deq(BoundedQueue.java:109)  
at java.base/java.lang.Thread.run(Thread.java:833)  
Exception in thread "Thread-27" java.lang.IllegalMonitorStateException  
at java.base/java.util.concurrent.locks.ReentrantLock$Sync.tryRelease(ReentrantLock.java:175)  
at java.base/java.util.concurrent.locks.AbstractQueuedSynchronizer.release(AbstractQueuedSynchronizer.java:1007)  
at java.base/java.util.concurrent.locks.ReentrantLock.unlock(ReentrantLock.java:494)  
at b.BoundedQueue.deq(BoundedQueue.java:109)  
at java.base/java.lang.Thread.run(Thread.java:833)  

</details>

---

### Exercitiu 2c


a) [Implementare](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx2/src/main/java/c/BoundedQueueSpinning.java#L9) 

Algoritmul pentru coada va functiona corect chiar daca folosim spinning in loc de wait, acestea avand acelasi efect,
dupa actualizarea valorii, thread-ul revenind, dar fara sa trebuiasca sa mai fie notificat in cazul spinningului.  
O alta diferenta ar fi ca prin folosirea spinningului in locul wait-ului, thread-ul aflat in asteptare nu va mai face release
la lock, el fiind cel care va continua atunci cand operatia asteptata va fi posibila.

b) [Implementare](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx2/src/main/java/c/BoundedQueueSpinningMixed.java#L9)


### Exercitiu 2d

[Implementare](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/6f8e8c106fa0c74832ad76dd96126fd3e1ea35c0/TpmEx2/src/main/java/d/UnboundedQueue.java#L7)

Verificarea pentru coada nevida din metoda deq() trebuie neaparat sa fie plasata in sectiunea protejata prin lock.  
Pentru a demonstra aceasta afirmatia, presupunem ca verificarea ar fi plasata in afara sectiunii protejate de lock si luam urmatorul caz:
 
Avem o coada cu un singur element si 2 deq-ari.  
Deq-ar 1 se afla in sectiunea critica, la inceput.  
Deq-ar 2 se afla inainte de lock si trece de conditia care verifica daca lista e goala.  
Deq-ar 2 asteapta pana ce Deq-ar 1 scoate elementul si elibereaza lock-ul.  
Deq-ar 2 intra in zona critica si nu mai tine cont de faptul ca lista e goala, rezultand intr-o eroare de executie (**NullPointerException**) la linia:
`result = head.next.value`  

In concluzie, desi tot o exceptie ar fi aruncata, functionarea algoritmului ar fi incorecta intrucat:  
Exceptia este diferita fata de cea la care ne-am fi asteptat din cod  
Exceptia este aruncata la un alt moment fata de cel la care ne-am astepta  
Exceptia este aruncata de la o alta linie de cod de cat ne-am fi asteptat

### Exercitiu 3

Pentru a optimiza algoritmul, introducem conceptul de versionare printr-o variabila atomica `version`, pe care o incrementam in operatiile
care modifica lista.  
Astfel, putem modifica validarea `if (validate(pred, current))`, sa verifice intai daca s-a modificat variabila `version`, obtinand urmatoarea validare:
`if (previousVersion == version.get() || validate(pred, current))`, unde variabila `previousVersion` reprezinta valoarea variabilei `version` dinaintea
obtinerii lock-urilor pe noduri.  
Putem observa imbunatatirea de performanta dintre cele doua versiuni in tabelul de mai jos.


Dupa rularea unui benchmark cu ajutorul urmatorului [cod](https://github.com/gabidiac11/multiprocessor-programming-techniques-java-homework-2/blob/316e1611163110b4634cbc33612fe57673db4062/TpmEx3/src/main/java/TestRun.java) a cate 20 de iteratii pentru fiecare algoritm:

| |   |add   |   |   | remove  |   |   | contains  |   |
|---|---|---|---|---|---|---|---|---|---|
|   |min_time(s)   |avg_time(s)   |max_time(s)   |min_time(s)   |avg_time(s)   |max_time(s)   |min_time(s)   |avg_time(s)   |max_time(s)   |
|Algoritm Original|12.68   | 16.27  | 17.06  | 0.12  | 0.59  | 0.79  | 5.09  | 26.68  | 35.92  |
|Algoritm Optimizat| 5.76  | 8.26  | 8.70  | 0.07  | 0.36  | 0.41  | 2.15  | 16.43  | 18.59  |
|Improvement|120%| 96%| 96%| 71% | 63%| 92%| 136%| 62% | 93%|

Mean improvement = (96 + 63 + 62) / 3 = 73.66%.  
Our optimized version of the list performed, on average, 73.66% faster than the original one.