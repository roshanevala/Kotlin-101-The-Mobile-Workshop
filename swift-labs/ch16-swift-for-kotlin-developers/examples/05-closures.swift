let minutes = [10, 20, 30]
let doubled: (Int) -> Int = { value in value * 2 }

print(minutes.map(doubled))
print(minutes.filter { $0 >= 20 }.reduce(0, +))
