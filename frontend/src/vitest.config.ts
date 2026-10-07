import { defineConfig } from 'vitest/config';

// Una sola identidad de Angular al trabajar con enlaces/unidades virtuales en Windows.
export default defineConfig({
  resolve: { preserveSymlinks: true, dedupe: ['@angular/core', '@angular/common', '@angular/compiler'] },
  test: { isolate: true },
});
