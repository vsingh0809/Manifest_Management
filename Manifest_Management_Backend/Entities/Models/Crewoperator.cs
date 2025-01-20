using System;
using System.Collections.Generic;

namespace ManifestPortal1.Entities.Models;

public partial class Crewoperator
{
    public int CrewId { get; set; }

    public string? Salutation { get; set; }

    public string? FirstName { get; set; }

    public string? LastName { get; set; }

    public string? ContactNumber { get; set; }

    public DateTime? Dob { get; set; }

    public string? Gender { get; set; }

    public string? CrewUsername { get; set; }

    public string? Email { get; set; }

    public string? Password { get; set; }

    public byte[]? CrewImage { get; set; }
}
