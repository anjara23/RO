package com.RO.CPM.service;
import com.RO.CPM.model.Task;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CPMService {
    private List<Task> tasks = new ArrayList<>();
    private Map<String, Task> taskMap = new HashMap<>();
    private int projetDur = 0;
    private Map<String, List<String>> successeurs = new HashMap<>();
    private Map<String, List<String>> precedents = new HashMap<>();


    public Map<String, List<String>> saveTasks(List<Task> taskList) {
        this.tasks.clear();
        taskMap.clear();
        successeurs.clear();
        precedents.clear();

        boolean hasSucc = taskList.stream().anyMatch(t -> t.getSucc() != null);

        for (Task task : taskList) {
            String cleanName = task.getNom().trim().toUpperCase();
            task.setNom(cleanName);
            task.setDateTot(0);
            task.setDateTard(0);
            task.setMarge(0);

            if (task.getSucc() != null) {
                task.setSucc(task.getSucc().stream()
                        .map(s -> s.trim().toUpperCase())
                        .collect(Collectors.toList()));
            }

            if (task.getPreced() != null) {
                task.setPreced(task.getPreced().stream()
                        .map(p -> p.trim().toUpperCase())
                        .collect(Collectors.toList()));
            }

            taskMap.put(cleanName, task);
            tasks.add(task);
        }

        if (hasSucc) {
            for (Task task : tasks) {
                String tName = task.getNom();
                List<String> sucs = task.getSucc();
                if (sucs != null) {
                    for (String succ : sucs) {
                        String cleanSucc = succ.trim().toUpperCase();
                        if (!cleanSucc.equals("FIN")) {
                            successeurs.computeIfAbsent(tName, k -> new ArrayList<>()).add(cleanSucc);
                            precedents.computeIfAbsent(cleanSucc, k -> new ArrayList<>()).add(tName);
                        }
                    }
                }
            }

            for (Task t : tasks) {
                List<String> preds = precedents.getOrDefault(t.getNom(), new ArrayList<>());
                if (preds.isEmpty()) preds.add("-");
                t.setPreced(preds);
            }
            for (Task t : tasks) {
                List<String> sucs = successeurs.getOrDefault(t.getNom(), new ArrayList<>());
                if (sucs.isEmpty()) sucs.add("FIN");
                t.setSucc(sucs);
            }

            for (Task t : tasks) {
                precedents.putIfAbsent(t.getNom(), t.getPreced());
            }

            int fictifCounter = 1;
            List<Task> tachesFictives = new ArrayList<>();
            Map<String, List<String>> precedentsCopy = new LinkedHashMap<>(precedents);

            for (Map.Entry<String, List<String>> entry : precedentsCopy.entrySet()) {
                String succ = entry.getKey();
                List<String> preds = entry.getValue();

                if (preds.size() > 1) {
                    boolean conflit = false;
                    for (String pred : preds) {
                        List<String> succsOfPred = successeurs.getOrDefault(pred, new ArrayList<>());
                        if (succsOfPred.size() > 1) {
                            conflit = true;
                            break;
                        }
                    }

                    if (conflit) {
                        for (String pred : new ArrayList<>(preds)) {
                            String nomFictif = "F" + fictifCounter++;
                            Task fictive = new Task(nomFictif, 0, List.of(pred));
                            fictive.setSucc(List.of(succ));

                            taskMap.put(nomFictif, fictive);
                            tachesFictives.add(fictive);

                            successeurs.get(pred).remove(succ);
                            successeurs.get(pred).add(nomFictif);

                            precedents.get(succ).remove(pred);
                            precedents.get(succ).add(nomFictif);

                            successeurs.put(nomFictif, List.of(succ));
                            precedents.put(nomFictif, List.of(pred));
                        }
                    }
                }
            }

            tasks.addAll(tachesFictives);
            return precedents;

        } else {
            for (Task task : tasks) {
                String tName = task.getNom();
                List<String> preds = task.getPreced();
                if (preds != null) {
                    for (String pred : preds) {
                        String cleanPred = pred.trim().toUpperCase();
                        if (!pred.equals("-")) {
                            successeurs.computeIfAbsent(cleanPred, k -> new ArrayList<>()).add(tName);
                            precedents.computeIfAbsent(tName, k -> new ArrayList<>()).add(cleanPred);
                        }
                    }
                }
            }

            for (Task t : tasks) {
                List<String> sucs = successeurs.getOrDefault(t.getNom(), new ArrayList<>());
                if (sucs.isEmpty()) sucs.add("FIN");
                t.setSucc(sucs);
            }
            for (Task t : tasks) {
                List<String> preds = precedents.getOrDefault(t.getNom(), new ArrayList<>());
                if (preds.isEmpty()) preds.add("-");
                t.setPreced(preds);
            }

            for (Task t : tasks) {
                successeurs.putIfAbsent(t.getNom(), t.getSucc());
            }

            int fictifCounter = 1;
            List<Task> tachesFictives = new ArrayList<>();
            Map<String, List<String>> precedentsCopy = new LinkedHashMap<>(precedents);

            for (Map.Entry<String, List<String>> entry : precedentsCopy.entrySet()) {
                String succ = entry.getKey();
                List<String> preds = entry.getValue();

                if (preds.size() > 1) {
                    boolean conflit = false;
                    for (String pred : preds) {
                        List<String> succsOfPred = successeurs.getOrDefault(pred, new ArrayList<>());
                        if (succsOfPred.size() > 1) {
                            conflit = true;
                            break;
                        }
                    }

                    if (conflit) {
                        for (String pred : new ArrayList<>(preds)) {
                            String nomFictif = "F" + fictifCounter++;
                            Task fictive = new Task(nomFictif, 0, List.of(pred));
                            fictive.setSucc(List.of(succ));

                            taskMap.put(nomFictif, fictive);
                            tachesFictives.add(fictive);

                            successeurs.get(pred).remove(succ);
                            successeurs.get(pred).add(nomFictif);

                            precedents.get(succ).remove(pred);
                            precedents.get(succ).add(nomFictif);

                            successeurs.put(nomFictif, List.of(succ));
                            precedents.put(nomFictif, List.of(pred));
                        }
                    }
                }
            }

            tasks.addAll(tachesFictives);
            return successeurs;
        }
    }

    public Map<String, Integer> datePlusTot() {
        Map<String, Integer> dateTotMap = new LinkedHashMap<>();
        dateTotMap.put("Début", 0);

        for (Task task : tasks) {
            if (task.getPreced() == null || task.getPreced().isEmpty() || (task.getPreced().size() > 0 && task.getPreced().get(0).equals("-"))) {
                task.setDateTot(0);
            } else {
                int maxPredDateTot = 0;
                for (String pred : task.getPreced()) {
                    if (pred.equals("-")) continue;
                    Task predTask = taskMap.get(pred);
                    if (predTask == null) {
                        throw new IllegalArgumentException("Prédecesseur inconnu: " + pred);
                    }
                    int predFinish = predTask.getDateTot() + predTask.getDuree();
                    maxPredDateTot = Math.max(maxPredDateTot, predFinish);
                }
                task.setDateTot(maxPredDateTot);
            }
        }

        boolean updated;
        do {
            updated = false;
            for (Task task : tasks) {
                if (task.getPreced() != null && !task.getPreced().isEmpty() && !task.getPreced().get(0).equals("-")) {
                    int maxPredDateTot = 0;
                    for (String pred : task.getPreced()) {
                        if (pred.equals("-")) continue;
                        Task predTask = taskMap.get(pred);
                        int predFinish = predTask.getDateTot() + predTask.getDuree();
                        maxPredDateTot = Math.max(maxPredDateTot, predFinish);
                    }
                    if (task.getDateTot() < maxPredDateTot) {
                        task.setDateTot(maxPredDateTot);
                        updated = true;
                    }
                }
            }
        } while (updated);

        Map<String, List<String>> successeurs = getSucc();
        Map<String, Integer> groupes = new LinkedHashMap<>();
        Set<String> dejaGroupees = new HashSet<>();

        for (Task task : tasks) {
            if (dejaGroupees.contains(task.getNom())) continue;

            List<String> succ = successeurs.get(task.getNom());
            if (succ == null) succ = List.of("FIN");

            List<String> groupe = new ArrayList<>();
            groupe.add(task.getNom());

            for (Task autre : tasks) {
                if (!autre.getNom().equals(task.getNom()) && !dejaGroupees.contains(autre.getNom())) {
                    List<String> autreSucc = successeurs.get(autre.getNom());
                    if (autreSucc == null) autreSucc = List.of("FIN");
                    if (new HashSet<>(succ).equals(new HashSet<>(autreSucc))) {
                        groupe.add(autre.getNom());
                    }
                }
            }

            String label = String.join("", groupe.stream().sorted().toList());
            int maxFin = groupe.stream()
                    .map(nom -> taskMap.get(nom).getDateTot() + taskMap.get(nom).getDuree())
                    .max(Integer::compareTo).orElse(0);

            groupes.put(label, maxFin);
            dejaGroupees.addAll(groupe);
        }

        groupes = groupes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        dateTotMap.putAll(groupes);

        int maxFinDate = tasks.stream()
                .filter(t -> t.getSucc() != null && t.getSucc().contains("FIN"))
                .mapToInt(t -> t.getDateTot() + t.getDuree())
                .max().orElse(0);

        dateTotMap.put("fin", maxFinDate);

        return dateTotMap;
    }

    public Map<String, Integer> datePlusTard() {
        Map<String, Integer> dateTardMap = new LinkedHashMap<>();

        int projetFin = tasks.stream()
                .filter(t -> t.getSucc() != null && t.getSucc().contains("FIN"))
                .mapToInt(t -> t.getDateTot() + t.getDuree())
                .max().orElse(0);

        for (Task task : tasks) {
            if (task.getSucc() != null && task.getSucc().contains("FIN")) {
                task.setDateTard(projetFin - task.getDuree());
            } else {
                task.setDateTard(Integer.MAX_VALUE);
            }
        }

        boolean updated;
        do {
            updated = false;
            for (Task task : tasks) {
                if (task.getSucc() != null && !task.getSucc().contains("FIN")) {
                    int minSuccTard = Integer.MAX_VALUE;
                    for (String succ : task.getSucc()) {
                        Task succTask = taskMap.get(succ);
                        if (succTask != null) {
                            minSuccTard = Math.min(minSuccTard, succTask.getDateTard());
                        }
                    }
                    int newTard = minSuccTard - task.getDuree();
                    if (newTard < task.getDateTard()) {
                        task.setDateTard(newTard);
                        updated = true;
                    }
                }
            }
        } while (updated);

        Map<String, List<Task>> groupesParPredecesseur = new LinkedHashMap<>();

        for (Task task : tasks) {
            if (task.getNom().startsWith("F") && task.getPreced().size() == 1) {
                String pred = task.getPreced().get(0);
                groupesParPredecesseur.computeIfAbsent(pred, k -> new ArrayList<>()).add(task);
            }
        }


        Map<String, Integer> resultatFinal = new LinkedHashMap<>();

        tasks.stream()
                .filter(t -> {
                    String nom = t.getNom();
                    return !nom.startsWith("F") || nom.length() == 1;
                })
                .sorted(Comparator.comparing(Task::getNom))
                .forEach(t -> resultatFinal.put(t.getNom(), t.getDateTard()));


        for (Map.Entry<String, List<Task>> entry : groupesParPredecesseur.entrySet()) {
            List<Task> fiGroup = entry.getValue();
            if (fiGroup.size() < 2) continue; // ignorer les isolés

            String nomGroupe = fiGroup.stream()
                    .map(Task::getNom)
                    .sorted()
                    .collect(Collectors.joining());

            int minDateTard = fiGroup.stream()
                    .mapToInt(Task::getDateTard)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            resultatFinal.put(nomGroupe, minDateTard);
        }

        tasks.stream()
                .filter(t -> {
                    List<String> succs = t.getSucc();
                    return succs == null || succs.isEmpty() || succs.contains("FIN");
                })
                .filter(t -> !t.getNom().startsWith("F")) // éviter les arcs fictifs
                .sorted(Comparator.comparing(Task::getNom))
                .forEach(t -> resultatFinal.put(t.getNom(), t.getDateTard()));


        resultatFinal.put("fin", projetFin);
        resultatFinal.put("Début", 0);

        return resultatFinal;
    }

    public List<String> cheminCritique() {
        List<String> chemin = new ArrayList<>();
        chemin.add("Début");

        // Trouver la tâche finale critique (celle qui mène à "FIN" avec la dateTot + durée maximale)
        Task finTask = null;
        int maxFin = 0;
        for (Task task : tasks) {
            if (task.getSucc() != null && task.getSucc().contains("FIN")) {
                int fin = task.getDateTot() + task.getDuree();
                if (fin > maxFin) {
                    maxFin = fin;
                    finTask = task;
                }
            }
        }

        if (finTask == null) return List.of();

        // Remonter les tâches critiques depuis la fin
        LinkedList<String> cheminInterne = new LinkedList<>();
        Task current = finTask;
        while (current != null) {
            cheminInterne.addFirst(current.getNom());
            Task predCritique = null;
            if (current.getPreced() != null) {
                for (String predName : current.getPreced()) {
                    if (predName.equals("-")) break;
                    Task pred = taskMap.get(predName);
                    if (pred != null && pred.getDateTot() + pred.getDuree() == current.getDateTot()) {
                        predCritique = pred;
                        break;
                    }
                }
            }
            current = predCritique;
        }

        chemin.addAll(cheminInterne);
        chemin.add("fin");

        return chemin;
    }

    public Map<String, Integer> marge() {
        Map<String, Integer> margeMap = new LinkedHashMap<>();

        // Étape 1 : calcul des marges pour les vraies tâches (non fictives)
        for (Task task : tasks) {
            String nom = task.getNom();
            // On exclut seulement les arcs fictifs (F1, F2, ...) mais pas la tâche "F"
            if (!nom.matches("F\\d+")) {
                int marge = task.getDateTard() - task.getDateTot();
                task.setMarge(marge);
                margeMap.put(nom, marge);
            }
        }

        // Étape 2 : regrouper les arcs fictifs par leur prédécesseur unique
        Map<String, List<Task>> groupesFictifs = new HashMap<>();

        for (Task task : tasks) {
            String nom = task.getNom();
            // Ici on cible les arcs fictifs explicitement
            if (nom.matches("F\\d+") && task.getPreced().size() == 1) {
                String pred = task.getPreced().get(0);
                groupesFictifs.computeIfAbsent(pred, k -> new ArrayList<>()).add(task);
            }
        }

        // Étape 3 : calculer la marge par groupe (ex: F1F5)
        for (Map.Entry<String, List<Task>> entry : groupesFictifs.entrySet()) {
            String pred = entry.getKey();
            List<Task> fictifs = entry.getValue();

            if (fictifs.size() < 2) continue; // ignorer les arcs fictifs isolés

            String nomGroupe = fictifs.stream()
                    .map(Task::getNom)
                    .sorted()
                    .collect(Collectors.joining());

            int dateTardMin = fictifs.stream()
                    .mapToInt(Task::getDateTard)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            Task predTask = taskMap.get(pred);
            if (predTask == null) continue;

            int marge = dateTardMin - (predTask.getDateTot() + predTask.getDuree());
            margeMap.put(nomGroupe, marge);
        }

        return margeMap;
    }

    public Map<String, List<String>> getSucc() {
        return successeurs;
    }
    public Map<String, List<String>> getPrec() {
        return precedents;
    }
}
