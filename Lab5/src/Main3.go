package main

import (
	"math/rand"
	"sync"
)

type Barier struct {
	n     int
	n_now int
	m     chan bool
}

func main() {
	arr1 := []int{1, 1, 1}
	arr2 := []int{1, 2, 0}
	arr3 := []int{2}
	s := [3]int{}
	f := false
	b := NewBarier(3)
	c := make(chan bool)
	var w sync.WaitGroup
	w.Add(4)
	go ThreadSumCompare(&s, c, &f, &w)
	go Thread(&arr1, &s[0], &f, b, c, &w)
	go Thread(&arr2, &s[1], &f, b, c, &w)
	go Thread(&arr3, &s[2], &f, b, c, &w)
	w.Wait()
}

func NewBarier(n int) *Barier {
	b := Barier{n: n, n_now: 0, m: make(chan bool, n)}
	return &b
}

func await(barier *Barier, c chan bool) {
	barier.n_now += 1
	if barier.n == barier.n_now {
		barier.n_now = 0
		c <- true
		<-c
		for i := 0; i < barier.n-1; i++ {
			barier.m <- true
		}
	} else {
		<-barier.m
	}
}

func Thread(arr *[]int, sum *int, flag *bool, barier *Barier, c chan bool, w *sync.WaitGroup) {
	for !*flag {
		*sum = 0
		for i := 0; i < len(*arr); i += 1 {
			*sum += (*arr)[i]
		}
		await(barier, c)
		if !*flag {
			if rand.Intn(2) == 0 {
				(*arr)[rand.Intn(len(*arr))] += 1
			} else {
				(*arr)[rand.Intn(len(*arr))] -= 1
			}
		}
	}
	(*w).Done()
}

func ThreadSumCompare(sumArr *[3]int, c chan bool, flag *bool, w *sync.WaitGroup) {
	for !*flag {
		<-c
		*flag = true
		println(sumArr[0], sumArr[1], sumArr[2])
		for i := 0; i < len(sumArr); i++ {
			for j := i + 1; j < len(sumArr); j++ {
				if sumArr[i] != sumArr[j] {
					*flag = false
				}
			}
		}
		c <- true
	}
	(*w).Done()
}
