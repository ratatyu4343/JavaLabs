package main

import (
	"math/rand"
	"sync"
	"time"
)

func main() {
	var t1 string
	var t2 string
	c := make(chan bool, 3)
	c2 := make(chan bool)
	var w sync.WaitGroup
	w.Add(2)
	go Poserednyk(c, c2, &t1, &t2, &w)
	c <- true
	go Person(c, c2, &t1, &t2, "Сірники", &w)
	go Person(c, c2, &t1, &t2, "Папір", &w)
	go Person(c, c2, &t1, &t2, "Тютюн", &w)

	w.Wait()
}

func Person(c chan bool, c2 chan bool, t1 *string, t2 *string, t3 string, w *sync.WaitGroup) {
	for <-c {
		if *t1 != t3 && *t2 != t3 {
			time.Sleep(1 * time.Second)
			print("Курець, що мав " + t3 + " викурив цигарку!\n")
			c2 <- true
		}
	}
	w.Done()
}

func Poserednyk(c chan bool, c2 chan bool, t1 *string, t2 *string, w *sync.WaitGroup) {
	t := []string{"Тютюн", "Папір", "Сірники"}
	for i := 0; i < 10; i++ {
		<-c2
		r := rand.Intn(3) % 3
		*t1 = t[r]
		r2 := rand.Intn(3) % 3
		if r == r2 {
			r2 = (r2 + 1) % 3
		}
		*t2 = t[r2]
		c <- true
		c <- true
		c <- true
	}
	c <- false
	c <- false
	c <- false
	w.Done()
}
