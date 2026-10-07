import { platformBrowser } from '@angular/platform-browser';
import { provideZoneChangeDetection } from '@angular/core';
import { AppModule } from './app/app-module';

// Los componentes NgModule existentes actualizan campos desde callbacks HTTP.
platformBrowser().bootstrapModule(AppModule, {
  applicationProviders: [provideZoneChangeDetection()],
})
  .catch(err => console.error(err));
