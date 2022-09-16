package main

import "fmt"
import "sync"

func fight(p1 int, p2 int, wg *sync.WaitGroup) int {
	defer wg.Done()
	if p1 > p2 {
		return p1
	} else {
		return p2
	}
}

func turnire(arr []int) int {
	for len(arr) > 1 {
		var wg = sync.WaitGroup{}
		var new_arr []int
		for i := 0; i < len(arr)-1; i += 2 {
			wg.Add(1)
			go func(i int) {
				new_arr = append(new_arr, fight(arr[i], arr[i+1], &wg))
			}(i)
		}
		wg.Wait()
		arr = new_arr
	}
	return arr[0]
}

func main() {
	powers := []int{10, 12, 13, 77, 11, 9, 4, 76}
	fmt.Println(turnire(powers))
}
