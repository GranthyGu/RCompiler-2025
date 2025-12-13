// enable bool type for ravel compatibility
#define bool _Bool

// declare libc functions manually (no includes!)
int printf(const char *fmt, ...);
int scanf(const char *fmt, ...);
void exit(int code);

// ==========================
//   printInt(i64)
// ==========================
void printlnInt(long long x) {
    printf("%lld\n", x);
}

// ==========================
//   getInt() -> i64
// ==========================
long long getInt() {
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

// ==========================
//   printInt(i64)
//   (no newline)
// ==========================
void printInt(long long x) {
    printf("%lld", x);
}