using System;
using System.Collections.Generic;
using ManifestPortal1.Entities.Models;
using Microsoft.EntityFrameworkCore;

namespace ManifestPortal1.Entities.EFCore;

public partial class TeamAContext : DbContext
{
    public TeamAContext()
    {
    }

    public TeamAContext(DbContextOptions<TeamAContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Crewoperator> Crewoperators { get; set; }

    public virtual DbSet<Guest> Guests { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        => optionsBuilder.UseMySQL("server=3.231.99.201;port=3306;database=team_a;user=user1;password=A1teamSKO;SslMode=none;");
        //=> optionsBuilder.UseMySQL("server=localhost;port=3306;database=team_a;user=root;password=12345;SslMode=none;");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Crewoperator>(entity =>
        {
            entity.HasKey(e => e.CrewId).HasName("PRIMARY");

            entity.ToTable("crewoperators");

            entity.HasIndex(e => e.CrewUsername, "CrewUsername").IsUnique();

            entity.HasIndex(e => e.Email, "Email").IsUnique();

            entity.Property(e => e.ContactNumber).HasMaxLength(15);
            entity.Property(e => e.Dob).HasMaxLength(6);
            entity.Property(e => e.FirstName).HasMaxLength(50);
            entity.Property(e => e.Gender).HasColumnType("enum('Male','Female','Other')");
            entity.Property(e => e.LastName).HasMaxLength(50);
            entity.Property(e => e.Password).HasMaxLength(255);
            entity.Property(e => e.Salutation).HasColumnType("enum('Mr','Mrs','Miss')");
        });

        modelBuilder.Entity<Guest>(entity =>
        {
            entity.HasKey(e => e.GuestId).HasName("PRIMARY");

            entity.ToTable("guests");

            entity.HasIndex(e => e.Email, "Email").IsUnique();

            entity.Property(e => e.BoardingPoint).HasMaxLength(100);
            entity.Property(e => e.Citizenship).HasMaxLength(50);
            entity.Property(e => e.CreatedBy).HasMaxLength(50);
            entity.Property(e => e.CreatedDate).HasMaxLength(6);
            entity.Property(e => e.DBarkDate)
                .HasMaxLength(6)
                .HasColumnName("dBarkDate");
            entity.Property(e => e.DestinationPoint).HasMaxLength(100);
            entity.Property(e => e.Dob)
                .HasMaxLength(6)
                .HasColumnName("DOB");
            entity.Property(e => e.Email).HasMaxLength(100);
            entity.Property(e => e.FirstName).HasMaxLength(50);
            entity.Property(e => e.Gender).HasColumnType("enum('Male','Female','Other')");
            entity.Property(e => e.IsDeleted)
                .HasMaxLength(1)
                .HasDefaultValueSql("'N'")
                .IsFixedLength()
                .HasColumnName("isDeleted");
            entity.Property(e => e.LastName).HasMaxLength(50);
            entity.Property(e => e.LastUpdatedBy).HasMaxLength(50);
            entity.Property(e => e.LastUpdatedDate).HasMaxLength(6);
            entity.Property(e => e.MBarkDate)
                .HasMaxLength(6)
                .HasColumnName("mBarkDate");
            entity.Property(e => e.MobileNo).HasMaxLength(15);
            entity.Property(e => e.Salutation).HasColumnType("enum('Mr','Mrs','Miss')");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
