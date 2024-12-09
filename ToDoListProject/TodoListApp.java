import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.*;

public class TodoListApp {
    public static void main(String[] args) throws AWTException {
        ArrayList<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int choice;

        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported! Desktop notifications will not work.");
        }

        do {
            System.out.println("\n=== To-Do List Application ===");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Mark Task as Complete");
            System.out.println("4. Delete Task");
            System.out.println("5. Set Reminder for a Task");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter the task: ");
                    String taskName = scanner.nextLine();
                    tasks.add(new Task(taskName));
                    System.out.println("Task added successfully!");
                    break;

                case 2:
                    if (tasks.isEmpty()) {
                        System.out.println("No tasks found!");
                    } else {
                        System.out.println("\nYour Tasks:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println((i + 1) + ". " + tasks.get(i));
                        }
                    }
                    break;

                case 3:
                    System.out.print("Enter the task number to mark as complete: ");
                    int completeIndex = scanner.nextInt() - 1;
                    if (completeIndex >= 0 && completeIndex < tasks.size()) {
                        tasks.get(completeIndex).setCompleted(true);
                        System.out.println("Task \"" + tasks.get(completeIndex).getName() + "\" marked as complete!");
                    } else {
                        System.out.println("Invalid task number!");
                    }
                    break;

                case 4:
                    System.out.print("Enter the task number to delete: ");
                    int deleteIndex = scanner.nextInt() - 1;
                    if (deleteIndex >= 0 && deleteIndex < tasks.size()) {
                        System.out.println("Task \"" + tasks.get(deleteIndex).getName() + "\" deleted successfully!");
                        tasks.remove(deleteIndex);
                    } else {
                        System.out.println("Invalid task number!");
                    }
                    break;

                case 5:
                    System.out.print("Enter the task number to set a reminder for: ");
                    int reminderIndex = scanner.nextInt() - 1;
                    if (reminderIndex >= 0 && reminderIndex < tasks.size()) {
                        System.out.println("Set reminder time:");
                        System.out.println("1. Seconds");
                        System.out.println("2. Minutes");
                        System.out.println("3. Hours");
                        System.out.print("Choose an option: ");
                        int timeOption = scanner.nextInt();
                        System.out.print("Enter the amount of time: ");
                        long timeAmount = scanner.nextInt();

                        long timeInMillis = 0;
                        switch (timeOption) {
                            case 1:
                                timeInMillis = timeAmount * 1000;
                                break;
                            case 2:
                                timeInMillis = timeAmount * 60 * 1000;
                                break;
                            case 3:
                                timeInMillis = timeAmount * 60 * 60 * 1000;
                                break;
                            default:
                                System.out.println("Invalid time option!");
                                continue;
                        }

                        Task task = tasks.get(reminderIndex);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (SystemTray.isSupported()) {
                                    try {
                                        displayNotification("Reminder", "Task: \"" + task.getName() + "\" is due!");
                                    } catch (Exception e) {
                                        System.out.println("[Error]: Unable to send notification.");
                                    }
                                } else {
                                    System.out.println("\n[Reminder]: Task \"" + task.getName() + "\" is due!");
                                }
                                playSound("notification.wav"); 
                                timer.cancel();
                            }
                        }, timeInMillis);

                        System.out.println("Reminder set for task \"" + task.getName() + "\"!");
                    } else {
                        System.out.println("Invalid task number!");
                    }
                    break;

                case 6:
                    System.out.println("Exiting To-Do List Application. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice! Please choose a valid option.");
            }
        } while (choice != 6);

        scanner.close();
    }

    private static void displayNotification(String title, String message) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "To-Do App");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("To-Do App Notification");
        tray.add(trayIcon);
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        tray.remove(trayIcon);
    }

    // my method to play sound
    private static void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("[Error]: Unable to play sound. " + e.getMessage());
        }
    }
}

class Task {
    private String name;
    private boolean isCompleted;

    public Task(String name) {
        this.name = name;
        this.isCompleted = false;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return name + (isCompleted ? " (Completed)" : "");
    }
}