using System;
using System.Collections.Generic;

namespace ManifestPortal1.Entities.Models;

public partial class Guest
{
    public int GuestId { get; set; }

    public string? FirstName { get; set; }

    public string? LastName { get; set; }

    public string? Salutation { get; set; }

    public string? Gender { get; set; }

    public DateTime? Dob { get; set; }

    public string? MobileNo { get; set; }

    public string? Email { get; set; }

    public string? Citizenship { get; set; }

    public byte[]? GuestImage { get; set; }

    public string? BoardingPoint { get; set; }

    public string? DestinationPoint { get; set; }

    public string? CreatedBy { get; set; }

    public DateTime? CreatedDate { get; set; }

    public string? LastUpdatedBy { get; set; }

    public DateTime? LastUpdatedDate { get; set; }

    public DateTime? MBarkDate { get; set; }

    public DateTime? DBarkDate { get; set; }

    public string? IsDeleted { get; set; }
}
