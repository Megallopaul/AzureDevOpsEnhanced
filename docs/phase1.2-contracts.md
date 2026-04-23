# Phase 1.2 - Documentation des Contrats

Document des comportements attendus des méthodes critiques. Cette documentation sert de référence pour les tests de caractérisation et le refactoring.

---

## 1. getPullRequests()

**Localisation** : `AzureDevOpsApiClient.kt:384`

### Signature
```kotlin
fun getPullRequests(
    status: String = "active",
    top: Int = 100,
): List<PullRequest>
```

### Contrat

**Entrées :**
- `status` : Filtre par statut de PR
  - Valeurs valides : `"active"`, `"completed"`, `"abandoned"`, `"all"`
  - Défaut : `"active"`
  - Comportement spécial : `"all"` est passé tel quel à l'API, autres valeurs utilisent `searchCriteria.status`
- `top` : Nombre maximum de PRs à retourner
  - Défaut : `100`
  - Utilise le paramètre `$top` de l'API Azure DevOps

**Sorties :**
- `List<PullRequest>` : Liste des PRs désérialisés depuis la réponse API
- Liste vide si aucun PR ne correspond aux critères
- **Ne retourne jamais `null`**

**Effets de bord :**
- Requête HTTP GET vers : `{baseUrl}/{project}/_apis/git/repositories/{repo}/pullrequests`
- Utilise `personalAccessToken` depuis la configuration
- Log l'exécution avec `logger.info()`

**Exceptions :**
- `AzureDevOpsApiException` :
  - Configuration invalide (token manquant)
  - Échec de la requête HTTP
  - Erreur de désérialisation JSON
  - Message d'erreur : `"Error while retrieving Pull Requests: {cause}"`

**Comportements critiques :**
1. **Validation config** : Appelle `requireValidConfig()` qui lance `AzureDevOpsApiException` si le token est manquant
2. **Gestion du statut** : Convertit `"all"` en paramètre API, sinon utilise la valeur telle quelle
3. **Parsing JSON** : Utilise `PullRequestListResponse` comme wrapper, extrait `value` (la liste)
4. **Error handling** : Catch `Exception` générique, enveloppe dans `AzureDevOpsApiException`

**Exemple d'usage :**
```kotlin
val activePRs = apiClient.getPullRequests(status = "active", top = 50)
val allCompletedPRs = apiClient.getPullRequests(status = "completed", top = 100)
```

---

## 2. getCommentThreads()

**Localisation** : `AzureDevOpsApiClient.kt:528`

### Signature
```kotlin
fun getCommentThreads(
    pullRequestId: Int,
    projectName: String? = null,
    repositoryId: String? = null,
): List<CommentThread>
```

### Contrat

**Entrées :**
- `pullRequestId` : ID numérique du PR
- `projectName` : Nom du projet (optionnel, utilise le projet courant si `null`)
- `repositoryId` : ID ou nom du repository (optionnel, utilise le repo courant si `null`)

**Sorties :**
- `List<CommentThread>` : Liste des threads de commentaires
- **Filtre automatiquement** les threads où `isDeleted == true`
- Liste vide si aucun thread
- **Ne retourne jamais `null`**

**Effets de bord :**
- Requête HTTP GET vers : `{baseUrl}/{project}/_apis/git/repositories/{repo}/pullrequests/{id}/threads`
- Utilise `personalAccessToken` depuis la configuration
- Log l'exécution avec `logger.info()`

**Exceptions :**
- `AzureDevOpsApiException` :
  - Configuration invalide
  - Échec de la requête HTTP
  - Erreur de désérialisation
  - Message : `"Error while retrieving comments: {cause}"`

**Comportements critiques :**
1. **Filtrage des threads supprimés** : `.filter { it.isDeleted != true }`
   - ⚠️ **CRITIQUE** : Ce filtrage est ESSENTIEL pour éviter des erreurs lors de l'affichage
   - Les threads marqués `isDeleted = true` correspondent à des fichiers supprimés ou déplacés
   - Sans ce filtre, `PullRequestCommentsService` tente d'afficher des commentaires sur des fichiers inexistants

2. **Fallback projet/repo** : Utilise la config courante si les paramètres optionnels sont `null`

3. **Parsing JSON** : Utilise `CommentThreadListResponse`, extrait `value`

**Exemple d'usage :**
```kotlin
// Threads du PR courant (projet/repo par défaut)
val threads = apiClient.getCommentThreads(pullRequestId = 123)

// Threads d'un PR dans un projet/repo spécifique
val threads = apiClient.getCommentThreads(
    pullRequestId = 123,
    projectName = "OtherProject",
    repositoryId = "other-repo"
)
```

---

## 3. filterDeletedFiles() (Pattern de filtrage)

**Localisation** : Multiple - voir usages dans le codebase

### Signature (pattern)
```kotlin
threads.filter { it.isDeleted != true && it.isActive() }
```

### Contrat

**Objectif :** Filtrer les threads de commentaires pour ne garder que ceux pertinents à afficher.

**Critères de filtrage :**

1. **`isDeleted != true`** (CRITIQUE)
   - Azure DevOps marque `isDeleted = true` quand :
     - Le fichier contenant le commentaire a été supprimé
     - Le fichier a été déplacé/renommé
     - Le thread a été supprimé
   - **Pourquoi filtrer** : Tenter d'afficher ces commentaires cause des erreurs Java (fichier introuvable)

2. **`isActive()`** (Méthode d'extension)
   - Vérifie que le thread n'est pas dans un état final (résolu/fermé)
   - Définition : `status != ThreadStatus.Fixed && status != ThreadStatus.Closed`

**Où ce pattern est utilisé :**
- ✅ `DiffViewerPanel.kt:442` - Filtrage pour l'affichage dans le diff
- ✅ `CommentsNavigatorToolWindow.kt:381` - Filtrage pour la tool window
- ✅ `TimelineModels.kt:88-89` - Filtrage pour la timeline
- ✅ `PrReviewTabPanel.kt:373,425,791` - Filtrage pour le review panel
- ✅ `FileTreePanel.kt:290` - Filtrage pour les badges
- ✅ `AzureDevOpsApiClient.kt:545` - Filtrage au niveau API

**⚠️ Bug historique (2026-04-22) :**
`PullRequestCommentsService.kt` était **la seule classe sans ce filtrage**, causant des erreurs lors de l'affichage des commentaires. Corrigé en ajoutant `filter { it.isDeleted != true }`.

**Exemple d'usage :**
```kotlin
val allThreads = apiClient.getCommentThreads(prId)
// Déjà filtré par isDeleted au niveau API

// Filtrage additionnel pour affichage
val activeThreads = allThreads.filter { it.isActive() }

// Filtrage complet (si API ne filtre pas)
val displayableThreads = allThreads.filter { 
    it.isDeleted != true && it.isActive() 
}
```

---

## 4. Path Normalization Logic

**Localisation** : `PullRequestCommentsService.kt:101-107` et autres

### Signature (pattern)
```kotlin
fun matchThreadToFilePath(
    threadPath: String,
    filePath: String,
): Boolean
```

### Contrat

**Objectif :** Faire correspondre le chemin d'un thread Azure DevOps avec le chemin local du fichier.

**Problème :**
- **Thread path** (Azure DevOps) : Relatif au repo, format Unix
  - Exemple : `/src/main.kt`, `/folder/file.cs`
  - Séparateur : `/` (forward slash)
  - Peut commencer par `/` ou non
- **File path** (Local) : Absolu, format OS
  - Exemple (Windows) : `C:\Users\dev\project\src\main.kt`
  - Exemple (macOS/Linux) : `/home/dev/project/src/main.kt`
  - Séparateur : `\` (Windows) ou `/` (Unix)

**Algorithme de correspondance :**

```kotlin
// 1. Normaliser le thread path
val normalizedThreadPath = threadPath
    .replace('/', '\\')      // Convertir slash en backslash
    .trimStart('\\')          // Retirer le slash initial

// 2. Normaliser le file path
val normalizedFilePath = file.path
    .replace('/', '\\')       // Uniformiser les séparateurs

// 3. Vérifier si le chemin de fichier se termine par le chemin du thread
val matches = normalizedFilePath.endsWith(
    normalizedThreadPath,
    ignoreCase = true         // Insensible à la casse
)
```

**Exemples :**

| Thread Path | File Path | Match | Explication |
|-------------|-----------|-------|-------------|
| `/src/main.kt` | `C:\project\src\main.kt` | ✅ | Fin de chemin correspond |
| `src/main.kt` | `C:\project\src\main.kt` | ✅ | Slash initial optionnel |
| `/src/main.kt` | `C:\project\lib\main.kt` | ❌ | `lib` ≠ `src` |
| `/folder/file.cs` | `C:\project\folder\file.cs` | ✅ | Correspond |
| `/folder/file.cs` | `C:\project\folder\FILE.cs` | ✅ | Ignore case |

**Où ce pattern est utilisé :**
- ✅ `PullRequestCommentsService.kt:101-107` - Filtrage des threads par fichier
- ✅ `DiffViewerPanel.kt` - Matching pour l'affichage dans le diff
- ✅ `CommentsNavigatorToolWindow.kt` - Matching pour la navigation

**Points d'attention :**
1. **Backward compatibility** : Certains anciens threads peuvent avoir des chemins dans des formats variés
2. **Insensitive à la casse** : Important pour les projets cross-platform (Windows/Linux)
3. **Séparateurs mixtes** : Gérer les chemins avec `/` et `\` mélangés

**Exemple d'usage :**
```kotlin
val fileThreads = threads.filter { thread ->
    val threadPath = thread.getFilePath() ?: return@filter false
    
    val normalizedThreadPath = threadPath.replace('/', '\\').trimStart('\\')
    val normalizedFilePath = file.path.replace('/', '\\')
    
    normalizedFilePath.endsWith(normalizedThreadPath, ignoreCase = true)
}
```

---

## Résumé des Contrats Critiques

| Méthode | Retour | Exceptions | Filtrage | Thread Safety |
|---------|--------|------------|----------|---------------|
| `getPullRequests()` | `List<PullRequest>` (jamais null) | `AzureDevOpsApiException` | Aucun | ❌ Non thread-safe |
| `getCommentThreads()` | `List<CommentThread>` (jamais null) | `AzureDevOpsApiException` | `isDeleted != true` | ❌ Non thread-safe |
| `filterDeletedFiles()` | Pattern de filtrage | N/A | `isDeleted != true && isActive()` | ✅ Thread-safe (opération pure) |
| `matchThreadToFilePath()` | `Boolean` | N/A | N/A | ✅ Thread-safe (opération pure) |

---

## Notes pour le Refactoring

### Priorité des contrats à préserver

1. **🔴 Critique** : Filtrage `isDeleted != true` dans `getCommentThreads()`
   - Doit absolument être préservé ou migré vers un autre endroit
   
2. **🔴 Critique** : Path normalization insensible à la casse
   - Essentiel pour le support cross-platform

3. **🟠 Important** : Gestion d'erreurs avec `AzureDevOpsApiException`
   - Actuellement catch `Exception` générique → à améliorer avec `Result<T>`

4. **🟡 Secondaire** : Logging avec `logger.info()`
   - Utile pour le debug, mais peut être externalisé

### Dettes techniques identifiées

1. **Exception handling** : Catch `Exception` au lieu de types spécifiques
2. **Null safety** : Certaines propriétés de modèles sont `Boolean?` au lieu de `Boolean`
3. **Thread safety** : Collections mutable partagées non thread-safe
4. **Couplage fort** : `getInstance()` pattern partout → à remplacer par DI
