# Corese-gui

Corese GUI est une interface graphique utilisateur développée pour faciliter l'utilisation du logiciel Corese, un moteur de recherche de ressources conceptuelles implémentant les standards du Web sémantique.

# Description

Le logiciel Corese, développé à l'INRIA, est utilisé pour expérimenter et rechercher dans le domaine du Web sémantique. Corese comprend plusieurs modules et interfaces, chacun offrant des fonctionnalités spécifiques :

- Corese-core: Le cœur du moteur, implémente les algorithmes pour le traitement et l'interrogation des données sémantiques.
- Corese-command: Interface en ligne de commande pour interagir avec Corese via des scripts et des commandes directes.
- Corese-server: Serveur permettant l'accès à Corese via des requêtes HTTP, idéal pour les environnements distribués.
- Corese-python: Module permettant l'intégration de Corese avec Python pour le développement et l'automatisation.
- Corese-gui: Interface graphique utilisateur améliorée pour une utilisation intuitive de Corese.

# Fonctionnalités de Corese GUI

Création, manipulation et analyse d'ontologies RDF.
Interrogation avancée des données RDF avec des requêtes complexes.
Interface utilisateur conviviale facilitant la visualisation et la modification des données sémantiques.
Support pour l'importation, la sérialisation, le raisonnement et la validation des données RDF.

# Corese Application - Issues and Tasks

## To Do

### Fonctionnalités à Implémenter

- **Lier Validation SHACL**  
  La validation SHACL doit être intégrée pour permettre la validation de schémas RDF à l'aide de SHACL. Actuellement, cette fonctionnalité est manquante.

- **Fonctionnalité "Reload File"**  
  La fonctionnalité pour recharger un fichier ne fonctionne pas correctement et doit être corrigée. Actuellement, elle est soit non implémentée, soit elle ne répond pas aux attentes.

- **Drag and Drop dans Query**  
  Le drag and drop pour réorganiser les onglets dans le `TabPane` initial ne fonctionne pas correctement. Le redéposer dans le `TabPane` initial n'a pas l'effet attendu.

- **Affichage Visuel pour les Requêtes Construct**  
  La liaison de la requête `CONSTRUCT` avec l'affichage visuel est problématique. Le code est en commentaire et nécessite une correction pour un affichage correct.

- **Affichage du Résultat XML pour les Requêtes**  
  L'affichage du résultat sous forme XML pour les requêtes ne fonctionne pas comme prévu. La fonctionnalité doit être vérifiée et corrigée.

- **Implémentation de Settings View**  
  La vue des paramètres (`SettingsView`) n'est pas encore implémentée. Il faut continuer à travailler sur cette partie pour ajouter les paramètres et options nécessaires.

- **Lier Data - Logs, RDF Rules, Stats**  
  La partie dédiée à la gestion des logs, des règles RDF et des statistiques dans la vue de données doit être intégrée. Ces éléments doivent être correctement liés et affichés.

## Bugs

- **Bugs dans les Onglets de l'Éditeur de Code**  
  Les onglets rencontrent des problèmes lorsqu'on navigue plusieurs fois entre les onglets Validation et Query. Cela entraîne l'ajout d'onglets supplémentaires dans l'éditeur de code. De plus, l'onglet "+" dans l'éditeur de code peut ne pas fonctionner correctement à certains moments, notamment après la suppression de tous les onglets ou un clic molette.

- **Problème d'Affichage après Insertion de Fichiers**  
  Après avoir inséré des fichiers dans l'onglet Data, et si l'onglet est quitté puis rouvert, les fichiers et le graph seront correctement conservés. Cependant, la partie dédiée à la visualisation des fichiers chargés peut afficher un contenu vide à ce moment-là.

## Notes

- Assurez-vous de tester chaque fonctionnalité après implémentation pour vérifier que les problèmes sont résolus et que les nouvelles fonctionnalités fonctionnent comme prévu.
- Documentez les changements effectués et les solutions apportées pour faciliter la maintenance future.

Pour toute question ou pour signaler des problèmes supplémentaires, veuillez contacter l'équipe de développement.
