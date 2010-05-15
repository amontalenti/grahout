// create model
def f = new File("model")
def fw = new FileWriter(f)
def r = new Random()

// random items?
for (int user = 0; user < 100; user++) {
    for (int useritem = 0; useritem < r.nextInt(5); useritem++) {
        int item = r.nextInt(100)
        fw.write("$user,$item\n")
    }
}
fw.flush()
