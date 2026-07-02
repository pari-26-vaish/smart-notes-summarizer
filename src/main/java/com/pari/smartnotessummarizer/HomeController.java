package com.pari.smartnotessummarizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private SummarizerService summarizerService;

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/summarize")
    public String summarize(@RequestParam("notes") String notes,
                            @RequestParam(value = "length", defaultValue = "medium") String length,
                            Model model) {
        String summary = summarizerService.summarize(notes, length);

        Note note = new Note(notes, summary);
        noteRepository.save(note);

        model.addAttribute("notes", notes);
        model.addAttribute("summary", summary);
        model.addAttribute("selectedLength", length);
        return "index";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("history", noteRepository.findAllByOrderByCreatedAtDesc());
        return "history";
    }

    @PostMapping("/history/delete/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteRepository.deleteById(id);
        return "redirect:/history";
    }

    @PostMapping("/history/clear")
    public String clearHistory() {
        noteRepository.deleteAll();
        return "redirect:/history";
    }
}