import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet, Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-layout',
  templateUrl: './app-layout.component.html',
  styleUrls: ['./app-layout.component.css'],
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterModule]
})
export class AppLayoutComponent {
  dark: boolean = false;
  isSideMenuOpen: boolean = false;
  isNotificationsMenuOpen: boolean = false;
  isProfileMenuOpen: boolean = false;
  isPagesMenuOpen: boolean = false;
  isModalOpen: boolean = false;
  trapCleanup: (() => void) | null = null;

  constructor(public router: Router) {
    this.dark = this.getThemeFromLocalStorage();
  }

  getThemeFromLocalStorage(): boolean {
    const storedTheme = localStorage.getItem('dark');
    if (storedTheme !== null) {
      return JSON.parse(storedTheme);
    }
    return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  setThemeToLocalStorage(value: boolean): void {
    localStorage.setItem('dark', JSON.stringify(value));
  }

  toggleTheme() {
    this.dark = !this.dark
    this.setThemeToLocalStorage(this.dark)
  }

  toggleSideMenu() {
    this.isSideMenuOpen = !this.isSideMenuOpen
  }
  closeSideMenu() {
    this.isSideMenuOpen = false
  }
  toggleNotificationsMenu() {
    this.isNotificationsMenuOpen = !this.isNotificationsMenuOpen
  }
  closeNotificationsMenu() {
    this.isNotificationsMenuOpen = false
  }
  toggleProfileMenu() {
    this.isProfileMenuOpen = !this.isProfileMenuOpen
  }
  closeProfileMenu() {
    this.isProfileMenuOpen = false
  }
  togglePagesMenu() {
    this.isPagesMenuOpen = !this.isPagesMenuOpen
  }
  openModal() {
    this.isModalOpen = true
    this.trapCleanup = this.focusTrap(document.querySelector('#modal'))
  }
  closeModal() {
    this.isModalOpen = false
    this.trapCleanup!()
  }

  focusTrap(element: any) {
    const focusableElements = getFocusableElements(element)
    const firstFocusableEl = focusableElements[0]
    const lastFocusableEl = focusableElements[focusableElements.length - 1]
  
    // Wait for the case the element was not yet rendered
    setTimeout(() => firstFocusableEl.focus(), 50)
  
    /**
     * Get all focusable elements inside `element`
     * @param {HTMLElement} element - DOM element to focus trap inside
     * @return {HTMLElement[]} List of focusable elements
     */
    function getFocusableElements(element: any = document) {
      return [
        ...element.querySelectorAll(
          'a, button, details, input, select, textarea, [tabindex]:not([tabindex="-1"])'
        ),
      ].filter((e) => !e.hasAttribute('disabled'))
    }
  
    function handleKeyDown(e: any) {
      const TAB = 9
      const isTab = e.key.toLowerCase() === 'tab' || e.keyCode === TAB
  
      if (!isTab) return
  
      if (e.shiftKey) {
        if (document.activeElement === firstFocusableEl) {
          lastFocusableEl.focus()
          e.preventDefault()
        }
      } else {
        if (document.activeElement === lastFocusableEl) {
          firstFocusableEl.focus()
          e.preventDefault()
        }
      }
    }
  
    element.addEventListener('keydown', handleKeyDown)
  
    return function cleanup() {
      element.removeEventListener('keydown', handleKeyDown)
    }
  }
  
}