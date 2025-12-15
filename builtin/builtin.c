// enable bool type for ravel compatibility
#define bool _Bool

// declare libc functions manually (no includes!)
int printf(const char *fmt, ...);
int scanf(const char *fmt, ...);
void exit(int code);

// ==========================
//   printInt(i64)
// ==========================
void printlnInt(int x) {
    printf("%d\n", x);
}

// ==========================
//   printInt(i32)
// ==========================
void printInt(int x) {
    printf("%d", x);
}

// ==========================
//   getInt() -> i32
// ==========================
int getInt() {
    long long x;
    scanf("%lld", &x);
    return x;
}

// ==========================
//   exit(i32)
//   (We wrap libc exit)
// ==========================
void builtin_exit(int code) {
    exit(code);
}