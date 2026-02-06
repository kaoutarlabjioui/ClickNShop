# 🛒 ClickNShop Frontend Guide

Bienvenue sur la documentation du projet frontend **ClickNShop**.
Ce guide a pour but d'accompagner le développement de l'interface utilisateur (React) qui consommera l'API Backend existante.

---

## 📑 Table des Matières

1. [Présentation du Projet](#1-présentation-du-projet)
2. [Prérequis Techniques](#2-prérequis-techniques)
3. [Installation & Lancement](#3-installation--lancement)
4. [Architecture du Projet](#4-architecture-du-projet)
5. [Consommation de l'API REST](#5-consommation-de-lapi-rest)
6. [Développement des Fonctionnalités](#6-développement-des-fonctionnalités)
7. [Gestion de l'État (State Management)](#7-gestion-de-létat-state-management)
8. [Tests Unitaires](#8-tests-unitaires)
9. [Tests End-to-End (E2E)](#9-tests-end-to-end-e2e)
10. [Bonnes Pratiques](#10-bonnes-pratiques)

---

## 1. Présentation du Projet

**ClickNShop** est une application B2B de gestion commerciale destinée à **MicroTech Maroc**. Elle permet de gérer :
- Un catalogue de produits informatiques.
- Un portefeuille de clients (B2B) avec des règles de tarification spécifiques.
- Le cycle de vie des commandes (Devis -> Commande -> Facture).
- Les paiements fractionnés (Acomptes, Reste à payer).

### Objectif Frontend
Construire une interface **React (SPA)** moderne, réactive et ergonomique, communiquant avec le Backend Spring Boot via des API REST.

### Technologies
- **Core**: React 18+, JavaScript (ES6+).
- **Build Tool**: Vite (Rapide et léger).
- **HTTP Client**: Axios (Recommandé) ou Fetch.
- **Routing**: React Router DOM.
- **UI Library**: (Optionnel) TailwindCSS, Material UI ou CSS Modules.
- **Testing**: Vitest (Compatible syntaxe Jasmine/Jest) & Cypress.

---

## 2. Prérequis Techniques

Avant de commencer, assurez-vous d'avoir :
- **Node.js** (v18 ou supérieur) : [Télécharger](https://nodejs.org/)
- **npm** (inclus avec Node) ou **yarn**.
- Un navigateur moderne (Chrome, Firefox, Edge).
- **Le Backend ClickNShop lancé** et fonctionnel (par défaut sur `http://localhost:8088` ou `8080`).

---

## 3. Installation & Lancement

Le projet est initialisé avec [Vite](https://vitejs.dev/).

### Installation des dépendances
Ouvrez un terminal dans le dossier `frontend/` et exécutez :

```bash
npm install
# ou
yarn install
```

### Lancement en développement
Pour lancer le serveur de développement local :

```bash
npm run dev
```
L'application sera accessible sur `http://localhost:5173`.

---

## 4. Architecture du Projet

Une structure claire garantit la maintenabilité. Voici l'organisation recommandée dans `src/` :

```text
src/
 ├─ assets/          # Images, fonts, styles globaux
 ├─ components/      # Composants réutilisables (UI pure)
 │   ├─ common/      # Boutons, Inputs, Loaders
 │   ├─ layout/      # Navbar, Sidebar, Footer
 │   └─ shared/      # Tables, Paginators
 ├─ pages/           # Vues principales (correspondent aux routes)
 │   ├─ products/    # ProductList, ProductForm...
 │   ├─ clients/     # ClientList, ClientDetails...
 │   └─ orders/      # OrderList, OrderCreate...
 ├─ services/        # Logique d'appel API (Axios)
 │   ├─ api.js       # Instance Axios configurée
 │   ├─ productService.js
 │   ├─ clientService.js
 │   └─ orderService.js
 ├─ context/         # Gestion d'état global (AuthContext, CartContext)
 ├─ hooks/           # Custom Hooks (useFetch, useAuth...)
 ├─ utils/           # Fonctions utilitaires (formatPrice, validators)
 ├─ constants/       # Config, Endpoints, Enums
 ├─ tests/           # Tests unitaires
 └─ App.jsx          # Configuration du Router
```

### Pourquoi cette structure ?
- **Separation of Concerns** : La vue (`pages/`) ne fait pas d'appels API directs, elle utilise les `services/`.
- **Réutilisabilité** : Les `components/` sont "dumb" (idiots) : ils reçoivent des props et affichent des données.

---

## 5. Consommation de l'API REST

Utilisez **Axios** pour les requêtes HTTP. Créez une instance centralisée dans `src/services/api.js`.

### Configuration Axios
```javascript
import axios from 'axios';

// Vérifiez le port de votre backend (8088 ou 8080)
const API_URL = 'http://localhost:8088/api'; 

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT (si authentification)
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

### Gestion des Erreurs
Gérez les codes HTTP standard pour une bonne UX :
- **400 Bad Request** : Formulaire invalide (afficher les erreurs de validation).
- **401 Unauthorized** : Rediriger vers `/login`.
- **404 Not Found** : Afficher une page ou un composant "Introuvable".
- **422 Unprocessable Entity** : Erreur métier (ex: stock insuffisant).
- **500 Server Error** : Afficher un message générique "Une erreur est survenue".

---

## 6. Développement des Fonctionnalités

### A. Gestion des Produits (`/products`)
- **Liste** : Afficher un tableau avec pagination (Server-side pagination recommandée).
- **Actions** :
  - **Créer** : Formulaire avec validation (Nom, Prix, Stock...).
  - **Modifier** : Pré-remplir le formulaire.
  - **Supprimer** : Confirmation avant suppression (Soft Delete côté back).
- **Formatage** : Les prix doivent toujours avoir 2 décimales (ex: `150.00 DH`).

### B. Gestion des Clients (`/clients`)
- **Liste & Recherche** : Filtrer par nom, email ou entreprise.
- **Édition** : Mettre à jour les informations de contact.
- **Détails** : Une vue dédiée affichant :
  - Infos client.
  - Historique des dernières commandes.
  - Statistiques (Chiffre d'affaires généré).

### C. Gestion des Commandes (`/orders`)
C'est le cœur du métier B2B.
- **Création de Commande** :
  - Sélection du client.
  - Ajout multiple de produits (Panier temporaire).
  - Modification des quantités en direct.
- **Calculs (dans `utils/calculations.js`)** :
  - **HT** (Hors Taxe) = Prix unitaire * Quantité.
  - **TVA** = HT * Taux (ex: 20%).
  - **TTC** (Toutes Taxes Comprises) = HT + TVA.
  - **Remises** : Appliquer si éligible.
- **Statuts** : Gérer les étapes `CREATED` -> `CONFIRMED` -> `DELIVERED` ou `CANCELED`.

### D. Paiements (`/payments`)
- La commande peut être payée en plusieurs fois.
- **Moyens** : Espèces, Chèque, Virement.
- **Logique UI** :
  - Afficher "Montant Total", "Déjà Payé", "Reste à Payer".
  - Bloquer la validation si `Montant Saisi > Reste à Payer`.
  - Mettre à jour le statut de commande (ex: `PAID`) si le reste est 0.

---

## 7. Gestion de l'État (State Management)

Pour cette application, l'association **Context API + Hooks** est souvent suffisante et plus simple que Redux.

### Quand utiliser le Context ?
- **AuthContext** : Stocker l'utilisateur connecté (`user`, `role`, `token`) et les méthodes `login`/`logout`.
- **CartContext** : Stocker les articles en cours d'ajout pour une nouvelle commande (avant validation).

### Quand utiliser Redux (Toolkit) ?
- Si la complexité des commandes devient très élevée (nombreux filtres interdépendants, cache complexe).
*Commencez par Context, migrez vers Redux si nécessaire.*

---

## 8. Tests Unitaires

Utilisez **Vitest** (compatible syntaxe Jasmine/Jest) pour tester la logique métier critique.

### Ce qu'il faut tester absolument :
1.  **Utils** (`src/utils/math.test.js`) :
    - Vérifier que `calculateTTC(100, 20)` retourne bien `120`.
    - Vérifier l'arrondi monétaire.
2.  **Services** : Mocker Axios pour vérifier que les bons endpoints sont appelés.
3.  **Composants Critiques** : Vérifier qu'un bouton "Payer" est désactivé si le montant est invalide.

Exemple de test (Syntace Jasmine-like) :
```javascript
import { describe, it, expect } from 'vitest';
import { calculateTTC } from '../utils/math';

describe('Calculs Financiers', () => {
    it('doit calculer le montant TTC correctement', () => {
        expect(calculateTTC(100, 20)).toBe(120);
    });
});
```

---

## 9. Tests End-to-End (E2E)

Utilisez **Cypress** ou **Playwright** pour valider les parcours utilisateurs complets.

### Scénarios Obligatoires :
1.  **Login** : L'utilisateur se connecte -> Redirection Dashboard.
2.  **Parcours Achat** :
    - Créer une commande.
    - Ajouter 2 produits.
    - Valider la commande.
    - Vérifier qu'elle apparaît dans la liste.
3.  **Gestion Client** : Créer un client -> Vérifier sa présence dans la liste.

---

## 10. Bonnes Pratiques & Conseils

- **Composants Purs** : Gardez vos composants petits. Si un fichier dépasse 200 lignes, découpez-le.
- **Nommage** : Variables en `camelCase`, Composants en `PascalCase`.
- **Git** : Faites des commits atomiques (`feat: add product form`, `fix: calculation error`).
- **Clean Code** : Pas de "magic numbers". Utilisez des constantes (`const TAX_RATE = 0.20`).
- **Feedback Utilisateur** : Utilisez des "Toasts" (notifications) pour confirmer les succès ("Commande créée !") ou les erreurs.

---

*Bon code sur ClickNShop !* 🚀
