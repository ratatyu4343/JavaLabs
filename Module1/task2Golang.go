/******************************************************************************

                            Online Go Lang Compiler.
                Code, Compile, Run and Debug Go Lang program online.
Write your code in this editor and press "Run" button to execute it.

*******************************************************************************/


package main
import (
    "sync"
    "math/rand"
    )
type Book struct {
    home bool
    reading bool
    name string
}

type Message struct {
    book string
    reader string
    read bool
    hom bool
    readerChan chan bool
}


func library(c chan Message, books [] Book, count int) {
    for true {
        flag := false
        msg := <- c
        i := 0
        for i < count {
            if msg.book == books[i].name {
                break
            }
            i += 1
        }
        if msg.read == true{
            if books[i].reading == false {
                if msg.hom == true && books[i].home == false {
                    println(msg.reader+" can` get book "+msg.book+" for reading in home(only room)")
                } else {
                    flag = true
                    books[i].reading = true
                    print(msg.reader+" get book "+msg.book+" for reading in")
                    if msg.hom {
                        print(" home\n")
                    } else {
                        print(" room\n")
                    }
                }
            } else {
                println(msg.reader+" can`t get book "+msg.book+" for reading...")
            }
        } else {
            if books[i].reading == true {
                books[i].reading = false
                println(msg.reader+" return "+msg.book)
            }
        }
        msg.readerChan <- flag
    }
}

func Reader(c chan Message, books [] Book, count int, name string, wg *sync.WaitGroup) {
    myChan := make(chan bool)
    mybooks := []string{}
    for i := 0; i < rand.Intn(15); i++ {
        b := books[rand.Intn(count)].name
        msg := Message{
            read : true,
            reader : name,
            hom : rand.Intn(2) == 0,
            book : b,
            readerChan : myChan,
        }
        c <- msg
        flag := <-myChan
        if flag {
            mybooks = append(mybooks, b)
        }
    }
    for i := 0; i < len(mybooks); i++ {
        msg := Message{
            read : false,
            reader : name,
            hom : true,
            book : mybooks[i],
            readerChan : myChan,
        }
        c <- msg
        <-myChan
    }
    wg.Done()
}

func main() {
    var BOOKS = []Book{Book{home:true,reading:false,name:"Book1",},
             Book{home:rand.Intn(2)==0,reading:false,name:"Book2",},
             Book{home:rand.Intn(2)==0,reading:false,name:"Book3",},
             Book{home:rand.Intn(2)==0,reading:false,name:"Book4",},
             Book{home:rand.Intn(2)==0,reading:false,name:"Book5",},}
    messageChan := make(chan Message, 3)
    var wg sync.WaitGroup
    go library(messageChan, BOOKS, 5)
    wg.Add(3)
    go Reader(messageChan, BOOKS, 5, "Reader1", &wg)
    go Reader(messageChan, BOOKS, 5, "Reader2", &wg)
    go Reader(messageChan, BOOKS, 5, "Reader3", &wg)
    wg.Wait()
}


