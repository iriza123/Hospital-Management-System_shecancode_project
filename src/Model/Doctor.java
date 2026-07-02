package Model;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicalLicenseNumber;

    private String specialization;

    private boolean active = true;

    @OneToOne
    private User user;

    @ManyToOne
    private Hospital hospital;
}


