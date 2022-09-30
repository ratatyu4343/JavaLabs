package main

import (
	"fmt"
	"github.com/yourbasic/graph"
	"math/rand"
	"sync"
)

type ChangeCost struct {
	start int
	end   int
	cost  int64
}

func main() {
	g := graph.New(5)
	var mutLock sync.RWMutex
	cAdd := make(chan ChangeCost)
	cDel := make(chan ChangeCost)
	cCost := make(chan ChangeCost)
	cFind := make(chan ChangeCost)
	var waitG sync.WaitGroup
	go changeCost(&waitG, cCost, g, &mutLock)
	go addWay(&waitG, cAdd, g, &mutLock)
	go deleteWay(&waitG, cDel, g, &mutLock)
	go findPath(cFind, g, &mutLock)
	for i := 0; i < 100; i++ {
		waitG.Wait()
		n1 := rand.Intn(4)
		if n1 == 0 {
			waitG.Add(1)
			m := ChangeCost{
				cost:  100,
				start: rand.Intn(5),
				end:   rand.Intn(5),
			}
			cCost <- m
		}
		if n1 == 1 {
			waitG.Add(1)
			m := ChangeCost{
				cost:  100,
				start: rand.Intn(5),
				end:   rand.Intn(5),
			}
			cAdd <- m
		}
		if n1 == 2 {
			waitG.Add(1)
			m := ChangeCost{
				cost:  100,
				start: rand.Intn(5),
				end:   rand.Intn(5),
			}
			cDel <- m
		}
		if n1 == 3 {
			m := ChangeCost{
				cost:  100,
				start: rand.Intn(5),
				end:   rand.Intn(5),
			}
			cFind <- m
		}
	}
}

func changeCost(waitG *sync.WaitGroup, c chan ChangeCost, g *graph.Mutable, lock *sync.RWMutex) {
	for true {
		s := <-c
		lock.Lock()
		g.AddCost(s.start, s.end, s.cost)
		waitG.Done()
		lock.Unlock()
	}
}

func addWay(waitG *sync.WaitGroup, c chan ChangeCost, g *graph.Mutable, lock *sync.RWMutex) {
	for true {
		s := <-c
		lock.Lock()
		g.AddCost(s.start, s.end, s.cost)
		waitG.Done()
		lock.Unlock()
	}
}

func deleteWay(waitG *sync.WaitGroup, c chan ChangeCost, g *graph.Mutable, lock *sync.RWMutex) {
	for true {
		s := <-c
		lock.Lock()
		g.Delete(s.start, s.end)
		waitG.Done()
		lock.Unlock()
	}
}

func findPath(c chan ChangeCost, g *graph.Mutable, lock *sync.RWMutex) {
	for true {
		s := <-c
		lock.RLock()
		path, dist := graph.ShortestPath(g, s.start, s.end)
		fmt.Println("Міста: ", path, " Ціна: ", dist)
		lock.RUnlock()
	}
}
