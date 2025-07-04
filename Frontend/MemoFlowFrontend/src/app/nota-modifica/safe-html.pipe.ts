// safe-html.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'safeHtml',
  standalone: true // Rendi il pipe standalone
})
export class SafeHtmlPipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer) { }

  transform(value: string | null | undefined): SafeHtml {
    // Se il valore è nullo o indefinito, ritorna una stringa vuota o un valore sicuro
    if (value === null || value === undefined) {
      return this.sanitizer.bypassSecurityTrustHtml('');
    }
    // Bypassa la sanificazione di sicurezza di Angular per l'HTML fornito
    return this.sanitizer.bypassSecurityTrustHtml(value);
  }
}